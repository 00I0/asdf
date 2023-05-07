package com.example.asdf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private final Context context;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final List<Item> data;
    private int lastPosition = -1;


    public ItemAdapter(Context context, List<Item> data) {
        this.context = context;
        this.data = data;
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item currentItem = data.get(position);
        holder.bindTo(currentItem);

        if (holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_row_left);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView description;
        private final ImageView picture;
        private final TextView price;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            name = itemView.findViewById(R.id.itemName);
            description = itemView.findViewById(R.id.itemDescription);
            picture = itemView.findViewById(R.id.itemPicture);
            price = itemView.findViewById(R.id.itemPrice);
        }

        void bindTo(Item item) {
            name.setText(item.getName());
            description.setText(item.getDescription());
            price.setText(item.getPrice() + " Ft");

            StorageReference path = storage.getReference().child(item.getPicture());
            Glide.with(context).load(path).into(picture);


            itemView.findViewById(R.id.add_to_cart)
                    .setOnClickListener(view -> ((ShoppingActivity) context).addItem(item));
            itemView.findViewById(R.id.delete)
                    .setOnClickListener(view -> ((ShoppingActivity) context).deleteItem(item));
        }
    }
}
