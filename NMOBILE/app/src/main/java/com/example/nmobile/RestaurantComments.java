package com.example.nmobile;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

        restaurantId = getIntent().getIntExtra("restaurant_id", -1);

        if (restaurantId != -1) {
            loadComments(restaurantId);
        }

        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());
        statisticsButton.setOnClickListener(v -> toggleStatistics());
    }

    private void loadComments(int restaurantId) {
        commentsContainer.removeAllViews();
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT comments.comment_id, comments.content, comments.rating, users.email " +
                "FROM comments " +
                "INNER JOIN users ON comments.user_id = users.id " +
                "WHERE comments.restaurant_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(restaurantId)});

        while (cursor.moveToNext()) {
            String commentId = cursor.getString(cursor.getColumnIndex("comment_id"));
            String comment = cursor.getString(cursor.getColumnIndex("content"));
            String userName = cursor.getString(cursor.getColumnIndex("email"));
            float rating = cursor.getFloat(cursor.getColumnIndex("rating"));

            TextView commentTextView = new TextView(this);
            commentTextView.setText(userName + ": " + comment + " (" + rating + " stars)");
            commentTextView.setPadding(8, 8, 8, 8);

            commentTextView.setOnClickListener(v -> {
                selectedCommentId = commentId;
                deleteButton.setVisibility(View.VISIBLE);
                highlightSelectedComment(v); // Đánh dấu comment đã chọn
            });

            commentsContainer.addView(commentTextView);
        }
        cursor.close();
    }

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

    private void deleteSelectedComment() {
        if (selectedCommentId != null) {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            db.delete("comments", "comment_id = ?", new String[]{selectedCommentId});

            Toast.makeText(this, "Comment deleted successfully!", Toast.LENGTH_SHORT).show();

            loadComments(restaurantId);
            deleteButton.setVisibility(View.GONE);
            selectedCommentId = null;

            db.close();
        } else {
            Toast.makeText(this, "No comment selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showStatistics() {
        commentsContainer.removeAllViews();
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
        goodHeader.setText("Good Reviews");
        goodHeader.setTextSize(20);
        goodHeader.setTextColor(Color.YELLOW);
        goodHeader.setPadding(8, 8, 8, 8);
        goodComments.addView(goodHeader);

        LinearLayout badComments = new LinearLayout(this);
        badComments.setOrientation(LinearLayout.VERTICAL);

        TextView badHeader = new TextView(this);
        badHeader.setText("Bad Reviews");
        badHeader.setTextSize(20);
        badHeader.setTextColor(Color.RED);
        badHeader.setPadding(8, 8, 8, 8);
        badComments.addView(badHeader);

        while (cursor.moveToNext()) {
            String comment = cursor.getString(cursor.getColumnIndex("content"));
            String userName = cursor.getString(cursor.getColumnIndex("email"));
            float rating = cursor.getFloat(cursor.getColumnIndex("rating"));

            TextView commentTextView = new TextView(this);
            commentTextView.setText(userName + ": " + comment + " (" + rating + " stars)");
            commentTextView.setPadding(8, 8, 8, 8);

            if (rating >= 3) {
                goodComments.addView(commentTextView);
            } else {
                badComments.addView(commentTextView);
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

    private void highlightSelectedComment(View selectedView) {
        for (int i = 0; i < commentsContainer.getChildCount(); i++) {
            View view = commentsContainer.getChildAt(i);
            if (view instanceof TextView) {
                if (view == selectedView) {
                    view.setBackgroundColor(Color.YELLOW); // Màu nền để đánh dấu comment đã chọn
                } else {
                    view.setBackgroundColor(Color.TRANSPARENT); // Gỡ bỏ màu nền cho các comment khác
                }
            }
        }
    }
}
