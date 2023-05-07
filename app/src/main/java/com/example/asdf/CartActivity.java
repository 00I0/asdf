package com.example.asdf;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class CartActivity extends AppCompatActivity {
    private final List<ItemAndCount> items = new ArrayList<>();
    private long totalCost;
    private TextView total;
    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        CartItemAdapter adapter = new CartItemAdapter(this, items);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


        String userId = Optional.ofNullable(auth.getCurrentUser()).map(FirebaseUser::getUid).orElse("<spam>");

        CompletableFuture.runAsync(() -> firestore.collection("Carts").document(userId).get()
                .addOnSuccessListener(doc -> {
                    List<String> idsInCart = (List<String>) doc.get("items");
                    if (idsInCart.isEmpty()) {
                        Toast.makeText(this, "A kosár megnyitásakkor a kosár nem lehet üres", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }

                    firestore.collection("Items").whereIn("id", idsInCart).limit(10).get()
                            .addOnSuccessListener(docs -> {
                                docs.forEach(doc1 -> {
                                    Item item = doc1.toObject(Item.class);
                                    items.add(new ItemAndCount(item, idsInCart.stream()
                                            .filter(s -> s.equals(item.getId()))
                                            .count()));
                                });
                                totalCost = items.stream()
                                        .mapToInt(itemAndCount -> (int) (Integer.parseInt(itemAndCount.item.getPrice()) * itemAndCount.count))
                                        .sum();

                                total = findViewById(R.id.textView);
                                total.setText("Összesen: " + totalCost + " Ft");

                                adapter.notifyDataSetChanged();
                            });
                }));

        recyclerView = findViewById(R.id.checkoutRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(adapter);
    }

    public void pay(View view) {
        String userId = Optional.ofNullable(auth.getCurrentUser()).map(FirebaseUser::getUid).orElse("<spam>");
        firestore.collection("Carts").document(userId).get().addOnSuccessListener(doc -> {
            firestore.collection("Carts").document(userId).set(Map.of("items", List.of(), "userId", userId));
        });

        Toast.makeText(this, "Köszönjük a vásárlást!", Toast.LENGTH_SHORT).show();
        finish();

    }

    private static class ItemAndCount {
        Item item;
        Long count;

        public ItemAndCount(Item item, Long count) {
            this.item = item;
            this.count = count;
        }
    }

    private static class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {
        private final Context context;
        private final List<ItemAndCount> data;
        private int lastPosition = -1;


        public CartItemAdapter(Context context, List<ItemAndCount> data) {
            this.context = context;
            this.data = data;
        }


        @NotNull
        @Override
        public CartItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull CartItemAdapter.ViewHolder holder, int position) {
            ItemAndCount currentItem = data.get(position);
            holder.bindTo(currentItem);

            if (holder.getAdapterPosition() > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_row_right);
                holder.itemView.startAnimation(animation);
                lastPosition = holder.getAdapterPosition();
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView name;
            private final TextView count;
            private final TextView price;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                name = itemView.findViewById(R.id.cart_item_name);
                count = itemView.findViewById(R.id.cart_item_count);
                price = itemView.findViewById(R.id.cart_item_price);
            }

            void bindTo(ItemAndCount itemAndCount) {
                name.setText(itemAndCount.item.getName());
                count.setText(itemAndCount.count.toString() + " db");
                price.setText(itemAndCount.count * Integer.parseInt(itemAndCount.item.getPrice()) + " Ft");
            }
        }
    }

}
