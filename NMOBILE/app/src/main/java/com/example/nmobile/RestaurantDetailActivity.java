package com.example.nmobile;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RestaurantDetailActivity extends AppCompatActivity {

    private ImageView imageViewDetail;
    private TextView nameTextView;
    private TextView locationTextView;
    private TextView typeTextView;
    private TextView detailsTextView;
    private RatingBar ratingBarDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        imageViewDetail = findViewById(R.id.imageViewDetail);
        nameTextView = findViewById(R.id.textViewName);
        locationTextView = findViewById(R.id.textViewLocation);
        typeTextView = findViewById(R.id.textViewType);
        detailsTextView = findViewById(R.id.textViewDetails);
        ratingBarDetail = findViewById(R.id.ratingBarDetail);

        int restaurantId = getIntent().getIntExtra("restaurant_id", -1);

        if (restaurantId != -1) {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(DatabaseHelper.TABLE_RESTAURANTS, null, DatabaseHelper.COLUMN_RESTAURANT_ID + "=?", new String[]{String.valueOf(restaurantId)}, null, null, null);

            if (cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESTAURANT_NAME));
                String location = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_LOCATION));
                String type = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TYPE));
                String details = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DETAILS));
                float rating = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESTAURANT_RATING));
                int imageResId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE_URL));

                nameTextView.setText(name);
                locationTextView.setText(location);
                typeTextView.setText(type);
                detailsTextView.setText(details);
                ratingBarDetail.setRating(rating);
                imageViewDetail.setImageResource(imageResId);
            }
            cursor.close();
        }
    }
}

