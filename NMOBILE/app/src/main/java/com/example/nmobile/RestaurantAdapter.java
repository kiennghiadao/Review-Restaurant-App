package com.example.nmobile;

import android.content.Context; // Nhập khẩu Context để truy cập vào các thành phần ứng dụng
import android.database.Cursor; // Nhập khẩu Cursor để làm việc với dữ liệu
import android.view.LayoutInflater; // Nhập khẩu LayoutInflater để tạo view từ layout XML
import android.view.View; // Nhập khẩu View để làm việc với các view
import android.view.ViewGroup; // Nhập khẩu ViewGroup để chứa các view
import android.widget.ImageView; // Nhập khẩu ImageView để hiển thị hình ảnh
import android.widget.TextView; // Nhập khẩu TextView để hiển thị văn bản
import androidx.annotation.NonNull; // Nhập khẩu Annotation để đảm bảo không null
import androidx.recyclerview.widget.RecyclerView; // Nhập khẩu RecyclerView để tạo danh sách cuộn
import android.content.Intent; // Nhập khẩu Intent để chuyển đổi Activity

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {
    private Context context; // Biến để lưu context
    private Cursor cursor; // Biến để lưu cursor chứa dữ liệu

    // Constructor nhận context và cursor
    public RestaurantAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    // Tạo ViewHolder mới
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view từ layout
        View view = LayoutInflater.from(context).inflate(R.layout.restaurant_item, parent, false);
        return new ViewHolder(view);// Trả về ViewHolder
    }

    // Liên kết dữ liệu với ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            //Lấy dữ liệu nhà hàng
            final int restaurantId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESTAURANT_ID));
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESTAURANT_NAME));
            String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DETAILS));
            int imageResId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE_URL));
            // Cập nhật dữ liệu cho ViewHolder
            holder.nameTextView.setText(name);
            holder.descriptionTextView.setText(description);

            // Kiểm tra và thiết lập hình ảnh
            if (imageResId != -1) {
                holder.imageView.setImageResource(imageResId);
            }

            // Thiết lập sự kiện click cho item
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, RestaurantDetailActivity.class);
                intent.putExtra("restaurant_id", restaurantId);// Gửi ID nhà hàng
                context.startActivity(intent);
            });
        }
    }

    // Trả về số lượng item trong cursor
    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    // Phương thức lấy cursor
    public Cursor getCursor() {return cursor;}

    // Lớp ViewHolder để quản lý các view trong một item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView descriptionTextView;
        ImageView imageView;

        // Constructor cho ViewHolder
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.restaurant_name);
            descriptionTextView = itemView.findViewById(R.id.restaurant_description);
            imageView = itemView.findViewById(R.id.restaurant_image);
        }
    }

}

