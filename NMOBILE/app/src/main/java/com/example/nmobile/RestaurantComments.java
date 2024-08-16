// RestaurantCommentsActivity.java
package com.example.nmobile;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RestaurantComments extends AppCompatActivity {

    private LinearLayout commentsContainer;
    private int restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_comments);

        commentsContainer = findViewById(R.id.commentsContainer);

        restaurantId = getIntent().getIntExtra("restaurant_id", -1);
        if (restaurantId != -1) {
            loadComments(restaurantId);
        }
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
}
