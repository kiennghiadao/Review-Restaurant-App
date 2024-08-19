package com.example.nmobile;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

public class ReviewHistory extends AppCompatActivity {

    private LinearLayout reviewContainer;
    private UserSessionManager userSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_history);

        reviewContainer = findViewById(R.id.reviewContainer);

        // Khởi tạo UserSessionManager để lấy thông tin user
        userSessionManager = new UserSessionManager(this);

        loadUserReviews();
    }

    private void loadUserReviews() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        int userId = userSessionManager.getUserId();

        String query = "SELECT comments.content, restaurants.name, comments.rating FROM comments " +
                "INNER JOIN restaurants ON comments.restaurant_id = restaurants.restaurant_id " +
                "WHERE comments.user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No reviews found.", Toast.LENGTH_SHORT).show();
        }

        while (cursor.moveToNext()) {
            String comment = cursor.getString(cursor.getColumnIndex("content"));
            String restaurantName = cursor.getString(cursor.getColumnIndex("name"));
            float rating = cursor.getFloat(cursor.getColumnIndex("rating"));

            TextView reviewTextView = new TextView(this);
            reviewTextView.setText("Restaurant: " + restaurantName + "\nRating: " + rating + "\nComment: " + comment);
            reviewTextView.setPadding(8, 8, 8, 8);

            reviewContainer.addView(reviewTextView);
        }

        cursor.close();
    }
}
