package com.example.nmobile;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RestaurantDetailActivity extends AppCompatActivity {

    private ImageView imageViewDetail;
    private TextView nameTextView;
    private TextView locationTextView;
    private TextView typeTextView;
    private TextView detailsTextView;
    private RatingBar ratingBarDetail;
    private Button addCommentButton;
    private LinearLayout commentsContainer;
    private LinearLayout addCommentLayout;
    private RatingBar commentRatingBar;
    private EditText commentEditText;
    private Button submitCommentButton;
    private Button cancelCommentButton;
    private int restaurantId;
    private UserSessionManager userSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        // Khởi tạo UserSessionManager
        userSessionManager = new UserSessionManager(this);

        // Liên kết các view từ layout
        imageViewDetail = findViewById(R.id.imageViewDetail);
        nameTextView = findViewById(R.id.textViewName);
        locationTextView = findViewById(R.id.textViewLocation);
        typeTextView = findViewById(R.id.textViewType);
        detailsTextView = findViewById(R.id.textViewDetails);
        ratingBarDetail = findViewById(R.id.ratingBarDetail);
        addCommentButton = findViewById(R.id.buttonAddComment);
        commentsContainer = findViewById(R.id.commentsContainer);
        addCommentLayout = findViewById(R.id.addCommentLayout);
        commentRatingBar = findViewById(R.id.commentRatingBar);
        commentEditText = findViewById(R.id.editTextComment);
        submitCommentButton = findViewById(R.id.buttonSubmitComment);
        cancelCommentButton = findViewById(R.id.buttonCancelComment);

        restaurantId = getIntent().getIntExtra("restaurant_id", -1);

        if (restaurantId != -1) {
            loadRestaurantData(restaurantId);
            loadComments(restaurantId);
        }

        // Thêm sự kiện cho nút thêm bình luận
        addCommentButton.setOnClickListener(v -> {
            addCommentLayout.setVisibility(View.VISIBLE);
        });

        // Thêm sự kiện cho nút gửi bình luận
        submitCommentButton.setOnClickListener(v -> {
            String comment = commentEditText.getText().toString().trim();
            float rating = commentRatingBar.getRating();
            if (rating > 5.0) {
                rating = 5.0f;
            }
            if (!comment.isEmpty() || rating > 0) {
                saveComment(comment, rating);
                Toast.makeText(RestaurantDetailActivity.this, "Comment added successfully!", Toast.LENGTH_SHORT).show();
                addCommentLayout.setVisibility(View.GONE);
                loadComments(restaurantId); // Làm mới danh sách bình luận ngay lập tức
            } else {
                Toast.makeText(RestaurantDetailActivity.this, "Please enter a comment or a rating.", Toast.LENGTH_SHORT).show();
            }
        });

        // Thêm sự kiện cho nút hủy bình luận
        cancelCommentButton.setOnClickListener(v -> {
            addCommentLayout.setVisibility(View.GONE);
        });
    }

    private void loadRestaurantData(int restaurantId) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_RESTAURANTS, null,
                DatabaseHelper.COLUMN_RESTAURANT_ID + "=?", new String[]{String.valueOf(restaurantId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            Restaurant restaurant = new Restaurant(
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESTAURANT_ID)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESTAURANT_NAME)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_LOCATION)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TYPE)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DETAILS)),
                    cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESTAURANT_RATING)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE_URL))
            );

            nameTextView.setText(restaurant.getName());
            locationTextView.setText(restaurant.getLocation());
            typeTextView.setText(restaurant.getType());
            detailsTextView.setText(restaurant.getDetails());
            ratingBarDetail.setRating(restaurant.getRating());
            imageViewDetail.setImageResource(restaurant.getImageResId());
        }
        cursor.close();
    }


    private void loadComments(int restaurantId) {
        commentsContainer.removeAllViews();
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT comments.content, users.email " +
                "FROM comments " +
                "INNER JOIN users ON comments.user_id = users.id " +
                "WHERE comments.restaurant_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(restaurantId)});

        while (cursor.moveToNext()) {
            String comment = cursor.getString(cursor.getColumnIndex("content"));
            String userName = cursor.getString(cursor.getColumnIndex("email"));

            TextView commentTextView = new TextView(this);
            commentTextView.setText(userName + ": " + comment);
            commentTextView.setPadding(8, 8, 8, 8);

            commentsContainer.addView(commentTextView);
        }
        cursor.close();
    }

    private void saveComment(String comment, float rating) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int userId = userSessionManager.getUserId();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_REVIEW_RESTAURANT_ID, restaurantId);
        values.put(DatabaseHelper.COLUMN_CONTENT, comment);
        values.put(DatabaseHelper.COLUMN_COMMENT_USER_ID, userId);
        values.put(DatabaseHelper.COLUMN_RATING, rating);

        db.insert(DatabaseHelper.TABLE_COMMENTS, null, values);

        updateRestaurantRating();

        db.close();
    }

    private void updateRestaurantRating() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String avgRatingQuery = "SELECT AVG(" + DatabaseHelper.COLUMN_RATING + ") as avg_rating " +
                "FROM " + DatabaseHelper.TABLE_COMMENTS + " " +
                "WHERE " + DatabaseHelper.COLUMN_REVIEW_RESTAURANT_ID + " = ?";
        Cursor cursor = db.rawQuery(avgRatingQuery, new String[]{String.valueOf(restaurantId)});
        float avgRating = 0;
        if (cursor.moveToFirst()) {
            avgRating = cursor.getFloat(cursor.getColumnIndex("avg_rating"));
        }
        cursor.close();

        if (avgRating > 5.0) {
            avgRating = 5.0f;
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_RESTAURANT_RATING, avgRating);
        db.update(DatabaseHelper.TABLE_RESTAURANTS, values, DatabaseHelper.COLUMN_RESTAURANT_ID + "=?", new String[]{String.valueOf(restaurantId)});

        loadRestaurantData(restaurantId);

        db.close();
    }
}

