package com.example.asdf;

import android.app.ActivityOptions;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ShoppingActivity extends AppCompatActivity {
    private final static String CHANNEL_ID = "my_channel_01";
    private FirebaseUser user;

    private NotificationChannel channel;
    private FrameLayout redCircle;
    private TextView countTextView;

    private RecyclerView recyclerView;
    private FirebaseAuth auth;
    private List<Item> items;
    private ItemAdapter adapter;

    private FirebaseFirestore firestore;
    private NotificationManager notificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);

        auth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.checkoutRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        items = new ArrayList<>();
        adapter = new ItemAdapter(this, items);
        firestore = FirebaseFirestore.getInstance();

        CompletableFuture.runAsync(() -> firestore.collection("Items").orderBy("id").limit(10).get()
                .addOnSuccessListener(docs -> {
                    docs.forEach(doc -> {
                        Item item = doc.toObject(Item.class);
                        items.add(item);
                    });

                    adapter.notifyDataSetChanged();
                }));

        recyclerView.setAdapter(adapter);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID, "Shopp notifications", NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(channel);
        }

        deleteItem(new Item("", "-999", "", "", ""));
        // There's no item with negative id, it just refreshes number of items in the cart
    }


    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            changeSpanCount(2);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            changeSpanCount(1);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        redCircle = rootView.findViewById(R.id.view_alert_red_circle);
        countTextView = rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(v -> onOptionsItemSelected(alertMenuItem));
        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlertIcon(List<String> itemsInCart) {
        int cartItems = itemsInCart.size();
        if (0 < cartItems) {
            countTextView.setText(String.valueOf(cartItems));
        } else {
            countTextView.setText("");
        }

        redCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_shopping, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.cart) {
            startActivity(new Intent(this, CartActivity.class));
            return true;
        }

        if (itemId == R.id.profile) {
            startActivity(new Intent(this, ProfileActivity.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void changeSpanCount(int spanCount) {
        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        assert layoutManager != null;
        layoutManager.setSpanCount(spanCount);
    }

    @SuppressWarnings("unchecked")
    public void deleteItem(Item item) {
        CompletableFuture.runAsync(() -> {
            String userId = Optional.ofNullable(auth.getCurrentUser()).map(FirebaseUser::getUid).orElse("<spam>");
            firestore.collection("Carts")
                    .document(userId)
                    .get().addOnSuccessListener(doc -> {
                        List<String> itemsInCart = (List<String>) doc.get("items");


                        assert itemsInCart != null;
                        itemsInCart.remove(item.getId());


                        if (userId.equals("<spam>"))
                            return;


                        firestore.collection("Carts").document(userId).set(Map.of(
                                "items", itemsInCart,
                                "userId", userId
                        ));
                        updateAlertIcon(itemsInCart);
                    });
        });
    }

    @SuppressWarnings("unchecked")
    public void addItem(Item item) {
        CompletableFuture.runAsync(() -> {
            String userId = Optional.ofNullable(auth.getCurrentUser()).map(FirebaseUser::getUid).orElse("<spam>");
            firestore.collection("Carts")
                    .document(userId)
                    .get().addOnSuccessListener(doc -> {
                        List<String> itemsInCart = (List<String>) doc.get("items");


                        assert itemsInCart != null;
                        itemsInCart.add(item.getId());


                        if (userId.equals("<spam>"))
                            return;


                        firestore.collection("Carts").document(userId).set(Map.of(
                                "items", itemsInCart,
                                "userId", userId
                        ));
                        updateAlertIcon(itemsInCart);

                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                                .setContentTitle("Termék a kosrába került")
                                .setSmallIcon(R.drawable.ic_shopping_cart)
                                .setAutoCancel(true)
                                .setContentText(String.format("Ez a termék került a kosárba: %s", item.getName()))
                                .setChannelId(CHANNEL_ID)
                                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_IMMUTABLE));


                        notificationManager.notify(0, mBuilder.build());
                    });
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (auth.getCurrentUser() == null) finish();

        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            deleteItem(new Item("", "-999", "", "", ""));
        });
        // There's no item with negative id, it just refreshes number of items in the cart
    }
}
