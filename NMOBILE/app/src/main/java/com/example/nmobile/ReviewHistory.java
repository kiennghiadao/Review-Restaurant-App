package com.example.nmobile;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

public class ReviewHistory extends AppCompatActivity {

    private LinearLayout reviewContainer;// Biến để chứa các đánh giá
    private UserSessionManager userSessionManager;// Biến để quản lý phiên làm việc của người dùng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_history);

        reviewContainer = findViewById(R.id.reviewContainer); // Khởi tạo LinearLayout từ layout

        // Khởi tạo UserSessionManager để lấy thông tin user
        userSessionManager = new UserSessionManager(this);

        loadUserReviews();// Gọi phương thức để tải đánh giá của người dùng
    }

    private void loadUserReviews() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        int userId = userSessionManager.getUserId(); // Lấy ID người dùng từ phiên làm việc
        // Truy vấn SQL để lấy đánh giá của người dùng
        String query = "SELECT comments.content, restaurants.name, comments.rating FROM comments " +
                "INNER JOIN restaurants ON comments.restaurant_id = restaurants.restaurant_id " +
                "WHERE comments.user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)}); // Thực hiện truy vấn

        if (cursor.getCount() == 0) { // Kiểm tra nếu không có đánh giá
            Toast.makeText(this, R.string.no_reviews_found, Toast.LENGTH_SHORT).show();
        }

        while (cursor.moveToNext()) { // Lặp qua các kết quả
            String comment = cursor.getString(cursor.getColumnIndex("content"));
            String restaurantName = cursor.getString(cursor.getColumnIndex("name"));
            float rating = cursor.getFloat(cursor.getColumnIndex("rating"));

            // Tạo đối tượng TextView để hiển thị đánh giá
            TextView reviewTextView = new TextView(this);

            // Thiết lập nội dung cho TextView
            String reviewText;
            if (comment != null && !comment.isEmpty()) {
                reviewText = getString(R.string.review_text, restaurantName, rating, comment);
            } else {
                reviewText = getString(R.string.review_text, restaurantName, rating, getString(R.string.no_comment_provided));
            }
            reviewTextView.setText(reviewText);

            // Đặt khoảng đệm cho TextView
            reviewTextView.setPadding(8, 8, 8, 8);
            // Thêm TextView vào LinearLayout
            reviewContainer.addView(reviewTextView);
        }

        cursor.close();
    }
}
