package com.example.nmobile;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.app.Dialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class activity_main extends AppCompatActivity {

    private EditText searchBar;
    private ImageButton profileButton;
    private String userRole;
    private RecyclerView recyclerView;
    private RestaurantAdapter adapter;
    private DatabaseHelper dbHelper;
    private Button manageRestaurantsButton;
    private ImageButton searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBar = findViewById(R.id.search_bar);
        searchButton = findViewById(R.id.search_button);

        // Truy xuất vai trò người dùng từ Intent
        Intent intent = getIntent();
        userRole = intent.getStringExtra("User Role");

        dbHelper = new DatabaseHelper(this);

        // Mở nút profile
        profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(view -> ShowProfileOptions());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchButton.setOnClickListener(view -> performSearch());

        loadRestaurants();
    }

    private void ShowProfileOptions() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_profile_options);

        // Set up common menu items
        Button viewReviewHistory = dialog.findViewById(R.id.view_review_history);
        Button viewVisitedRestaurants = dialog.findViewById(R.id.view_visited_restaurants);
        Button logout = dialog.findViewById(R.id.logout);

        viewReviewHistory.setOnClickListener(view -> dialog.dismiss());

        viewVisitedRestaurants.setOnClickListener(view -> dialog.dismiss());

        logout.setOnClickListener(view -> {
            Intent intent = new Intent(activity_main.this, activity_login.class);
            startActivity(intent);
            finish();
            dialog.dismiss();
        });

        if ("admin".equals(userRole)) {
            Button manageUsers = dialog.findViewById(R.id.manage_users);
            Button manageCategories = dialog.findViewById(R.id.manage_categories);
            manageRestaurantsButton = dialog.findViewById(R.id.manage_restaurants);
            Button manageReviews = dialog.findViewById(R.id.manage_reviews);

            manageUsers.setVisibility(View.VISIBLE);
            manageCategories.setVisibility(View.VISIBLE);
            manageRestaurantsButton.setVisibility(View.VISIBLE);
            manageReviews.setVisibility(View.VISIBLE);

            manageUsers.setOnClickListener(v -> {
                Intent intent = new Intent(activity_main.this, Manage_Users.class);
                startActivity(intent);
            });

            manageCategories.setOnClickListener(v -> {
                // Handle manage categories
            });

            manageRestaurantsButton.setOnClickListener(v -> {
                Intent intent = new Intent(activity_main.this, AddRestaurantActivity.class);
                startActivity(intent);
            });

            manageReviews.setOnClickListener(v -> {
                Intent intent = new Intent(activity_main.this, ManageReviews.class);
                startActivity(intent);
            });
        }

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRestaurants(); // Tải lại danh sách quán ăn từ cơ sở dữ liệu
    }

    private void performSearch() {
        String query = searchBar.getText().toString();
        Cursor cursor = dbHelper.searchRestaurants(query);
        adapter = new RestaurantAdapter(this, cursor);
        recyclerView.setAdapter(adapter);
    }

    private void loadRestaurants() {
        Cursor cursor = dbHelper.getAllRestaurants();
        adapter = new RestaurantAdapter(this, cursor);
        recyclerView.setAdapter(adapter);
    }
}
