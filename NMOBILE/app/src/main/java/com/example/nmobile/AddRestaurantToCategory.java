package com.example.nmobile;

import android.database.Cursor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.Set;

public class AddRestaurantToCategory extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private RestaurantAdapter adapter;
    private long categoryId;
    private Set<Long> selectedRestaurantIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_restaurant_to_category);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.restaurant_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lấy categoryId từ Intent
        categoryId = getIntent().getLongExtra("categoryId", -1);

        loadRestaurants();

        Button addSelectedButton = findViewById(R.id.add_selected_restaurants_button);
        addSelectedButton.setOnClickListener(v -> addSelectedRestaurantsToCategory());

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    View childView = rv.findChildViewUnder(e.getX(), e.getY());
                    if (childView != null) {
                        int position = rv.getChildAdapterPosition(childView);
                        Cursor cursor = adapter.getCursor();
                        if (cursor != null && cursor.moveToPosition(position)) {
                            long restaurantId = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESTAURANT_ID));
                            toggleSelection(restaurantId);
                        }
                    }
                }
                return true;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });
    }

    private void loadRestaurants() {
        Cursor cursor = dbHelper.getAllRestaurants();
        adapter = new RestaurantAdapter(this, cursor);
        recyclerView.setAdapter(adapter);
    }

    private void toggleSelection(long restaurantId) {
        if (selectedRestaurantIds.contains(restaurantId)) {
            selectedRestaurantIds.remove(restaurantId);
        } else {
            selectedRestaurantIds.add(restaurantId);
        }
    }

    private void addSelectedRestaurantsToCategory() {
        if (!selectedRestaurantIds.isEmpty()) {
            for (long restaurantId : selectedRestaurantIds) {
                dbHelper.addRestaurantToCategory(categoryId, restaurantId);
            }
            Toast.makeText(this, "Added selected restaurants to category", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "No restaurants selected", Toast.LENGTH_SHORT).show();
        }
    }
}
