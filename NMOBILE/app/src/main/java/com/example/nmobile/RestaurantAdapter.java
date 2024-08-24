package com.example.nmobile;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {
    private Context context;
    private Cursor cursor;

    public RestaurantAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.restaurant_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            final int restaurantId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESTAURANT_ID));
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESTAURANT_NAME));
            String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DETAILS));
            int imageResId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE_URL));

            holder.nameTextView.setText(name);
            holder.descriptionTextView.setText(description);

            if (imageResId != -1) {
                holder.imageView.setImageResource(imageResId);
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, RestaurantDetailActivity.class);
                intent.putExtra("restaurant_id", restaurantId);
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    // Lấy cursor
    public Cursor getCursor() {return cursor;}

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView descriptionTextView;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.restaurant_name);
            descriptionTextView = itemView.findViewById(R.id.restaurant_description);
            imageView = itemView.findViewById(R.id.restaurant_image);
        }
    }

}

