package com.example.nmobile;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageReviews extends AppCompatActivity {

    private ListView restaurantListView;
    private List<Map<String, Object>> restaurantData;
    private static final String[] FROM = {"name", "image"};
    private static final int[] TO = {R.id.restaurant_name, R.id.restaurant_image};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_reviews);

        restaurantListView = findViewById(R.id.restaurantListView);
        restaurantData = new ArrayList<>();

        loadRestaurantList();

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                restaurantData,
                R.layout.restaurant_item, // Layout item
                FROM,
                TO
        );

        restaurantListView.setAdapter(adapter);

        restaurantListView.setOnItemClickListener((adapterView, view, position, id) -> {
            int restaurantId = (int) restaurantData.get(position).get("id");
            Intent intent = new Intent(ManageReviews.this, RestaurantComments.class);
            intent.putExtra("restaurant_id", restaurantId);
            startActivity(intent);
        });
    }

    private void loadRestaurantList() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.getAllRestaurants();

        while (cursor.moveToNext()) {
            int restaurantId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESTAURANT_ID));
            String restaurantName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESTAURANT_NAME));
            int restaurantImage = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE_URL));

            Map<String, Object> data = new HashMap<>();
            data.put("id", restaurantId);
            data.put("name", restaurantName);
            data.put("image", restaurantImage);

            restaurantData.add(data);
        }
        cursor.close();
    }
}
