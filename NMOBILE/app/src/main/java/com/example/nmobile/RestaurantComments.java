package com.example.nmobile;

import android.database.Cursor; // Nhập khẩu Cursor để làm việc với dữ liệu
import android.database.sqlite.SQLiteDatabase; // Nhập khẩu SQLiteDatabase để tương tác với cơ sở dữ liệu
import android.graphics.Color; // Nhập khẩu Color để sử dụng màu sắc
import android.os.Bundle; // Nhập khẩu Bundle để truyền dữ liệu giữa các Activity
import android.view.View; // Nhập khẩu View để tạo và quản lý các view
import android.widget.Button; // Nhập khẩu Button để sử dụng các nút
import android.widget.LinearLayout; // Nhập khẩu LinearLayout để chứa các view theo chiều dọc
import android.widget.TextView; // Nhập khẩu TextView để hiển thị văn bản
import android.widget.Toast; // Nhập khẩu Toast để hiển thị thông báo ngắn
import android.app.AlertDialog; // Nhập khẩu AlertDialog để hiển thị hộp thoại xác nhận
import androidx.appcompat.app.AppCompatActivity; // Nhập khẩu AppCompatActivity để sử dụng tính năng Activity

public class RestaurantComments extends AppCompatActivity {

    private LinearLayout commentsContainer;
    private int restaurantId;
    private String selectedCommentId;
    private Button deleteButton;
    private Button statisticsButton;
    private boolean isStatisticsVisible = false; // Biến trạng thái để theo dõi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_comments);

        commentsContainer = findViewById(R.id.commentsContainer);
        deleteButton = findViewById(R.id.btnDeleteComment);
        statisticsButton = findViewById(R.id.btnStatistic);

        // Nhận ID nhà hàng từ Intent
        restaurantId = getIntent().getIntExtra("restaurant_id", -1);

        // Nếu ID hợp lệ, tải bình luận
        if (restaurantId != -1) {
            loadComments(restaurantId);
        }

        // Thiết lập sự kiện click cho các nút
        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());
        statisticsButton.setOnClickListener(v -> toggleStatistics());
    }

    private void loadComments(int restaurantId) {
        commentsContainer.removeAllViews();
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Tạo truy vấn SQL để lấy bình luận
        String query = "SELECT comments.comment_id, comments.content, comments.rating, users.email " +
                "FROM comments " +
                "INNER JOIN users ON comments.user_id = users.id " +
                "WHERE comments.restaurant_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(restaurantId)});

        if (cursor.getCount() == 0) {
            // Nếu không có bình luận, hiển thị thông báo
            TextView noCommentsTextView = new TextView(this);
            noCommentsTextView.setText(R.string.no_comments_found);
            noCommentsTextView.setPadding(8, 8, 8, 8);
            commentsContainer.addView(noCommentsTextView);
        }

        while (cursor.moveToNext()) {
            String commentId = cursor.getString(cursor.getColumnIndex("comment_id"));
            String comment = cursor.getString(cursor.getColumnIndex("content"));
            String userName = cursor.getString(cursor.getColumnIndex("email"));
            float rating = cursor.getFloat(cursor.getColumnIndex("rating"));

            // Tạo một TextView mới để hiển thị bình luận
            TextView commentTextView = new TextView(this);
            commentTextView.setText(userName + ": " + (comment != null ? comment : getString(R.string.no_comment_provided)) + " (" + rating + " Stars)");
            commentTextView.setPadding(8, 8, 8, 8);

            // Thiết lập sự kiện click cho bình luận
            commentTextView.setOnClickListener(v -> {
                selectedCommentId = commentId; // Ghi nhớ ID bình luận đã chọn
                deleteButton.setVisibility(View.VISIBLE); // Hiện nút xóa
                highlightSelectedComment(v); // Đánh dấu comment đã chọn
            });

            // Thêm bình luận vào commentsContainer
            commentsContainer.addView(commentTextView);
        }

        cursor.close();
    }

    // Phương thức hiển thị hộp thoại xác nhận xóa bình luận
    private void showDeleteConfirmationDialog() {
        if (selectedCommentId != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmed deletion?")
                    .setMessage("Do you want to delete this comment?")
                    .setPositiveButton("OK", (dialog, which) -> deleteSelectedComment())
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }
    // Phương thức xóa bình luận đã chọn
    private void deleteSelectedComment() {
        if (selectedCommentId != null) {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Xóa bình luận
            db.delete("comments", "comment_id = ?", new String[]{selectedCommentId});

            Toast.makeText(this, "Comment deleted successfully!", Toast.LENGTH_SHORT).show();

            loadComments(restaurantId);// Tải lại các bình luận
            deleteButton.setVisibility(View.GONE);// Ẩn nút xóa
            selectedCommentId = null;

            db.close();
        } else {
            Toast.makeText(this, "No comment selected!", Toast.LENGTH_SHORT).show();
        }
    }
    // Phương thức hiển thị thống kê
    private void showStatistics() {
        commentsContainer.removeAllViews(); // Xóa tất cả các view cũ
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT content, rating, users.email " +
                "FROM comments " +
                "INNER JOIN users ON comments.user_id = users.id " +
                "WHERE comments.restaurant_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(restaurantId)});

        LinearLayout goodComments = new LinearLayout(this);
        goodComments.setOrientation(LinearLayout.VERTICAL);

        TextView goodHeader = new TextView(this);
        goodHeader.setText(R.string.good_reviews);
        goodHeader.setTextSize(20);
        goodHeader.setTextColor(Color.BLUE);
        goodHeader.setPadding(8, 8, 8, 8);
        goodComments.addView(goodHeader);

        LinearLayout badComments = new LinearLayout(this);
        badComments.setOrientation(LinearLayout.VERTICAL);

        TextView badHeader = new TextView(this);
        badHeader.setText(R.string.bad_reviews);
        badHeader.setTextSize(20);
        badHeader.setTextColor(Color.RED);
        badHeader.setPadding(8, 8, 8, 8);
        badComments.addView(badHeader);

        while (cursor.moveToNext()) {
            String comment = cursor.getString(cursor.getColumnIndex("content"));
            String userName = cursor.getString(cursor.getColumnIndex("email"));
            float rating = cursor.getFloat(cursor.getColumnIndex("rating"));

            TextView commentTextView = new TextView(this);
            commentTextView.setText(userName + ": " + (comment != null ? comment : getString(R.string.no_comment_provided)) + " (" + rating + " stars)");
            commentTextView.setPadding(8, 8, 8, 8);

            if (rating >= 3) {
                goodComments.addView(commentTextView); // Thêm vào bình luận tốt
            } else {
                badComments.addView(commentTextView); // Thêm vào bình luận xấu
            }
        }

        cursor.close();
        commentsContainer.addView(goodComments);
        commentsContainer.addView(badComments);
    }

    private void toggleStatistics() {
        if (isStatisticsVisible) {
            // Nếu phần thống kê đang hiển thị, thì ẩn nó và quay lại hiển thị các bình luận
            loadComments(restaurantId);
            isStatisticsVisible = false;
            statisticsButton.setText("Show Statistics");
        } else {
            // Nếu phần thống kê không hiển thị, thì hiện nó
            showStatistics();
            isStatisticsVisible = true;
            statisticsButton.setText("Hide Statistics");
        }
    }
    // Phương thức đánh dấu bình luận đã chọn
    private void highlightSelectedComment(View selectedView) {
        for (int i = 0; i < commentsContainer.getChildCount(); i++) {
            // Lấy từng view trong commentsContainer
            View view = commentsContainer.getChildAt(i);
            if (view instanceof TextView) {// Kiểm tra xem view có phải là TextView không
                if (view == selectedView) {
                    view.setBackgroundColor(Color.YELLOW); // Màu nền để đánh dấu comment đã chọn
                } else {
                    view.setBackgroundColor(Color.TRANSPARENT); // Gỡ bỏ màu nền cho các comment khác
                }
            }
        }
    }
}
