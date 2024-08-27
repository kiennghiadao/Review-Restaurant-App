package com.example.nmobile;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.app.Dialog;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import android.util.SparseArray;

public class activity_main extends AppCompatActivity {

    private EditText searchBar;
    private ImageButton profileButton;
    private String userRole;
    private RecyclerView recyclerView;
    private RestaurantAdapter adapter;
    private DatabaseHelper dbHelper;
    private Button manageRestaurantsButton;
    private ImageButton searchButton;
    private Spinner categorySpinner;
    private SparseArray<Long> categoryIds; // Lưu trữ ID danh mục

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBar = findViewById(R.id.search_bar);
        searchButton = findViewById(R.id.search_button);
        profileButton = findViewById(R.id.profile_button);
        recyclerView = findViewById(R.id.recyclerView);
        categorySpinner = findViewById(R.id.category_spinner);

        // Truy xuất vai trò người dùng từ Intent
        Intent intent = getIntent();
        userRole = intent.getStringExtra("User Role");

        dbHelper = new DatabaseHelper(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        profileButton.setOnClickListener(view -> ShowProfileOptions());
        searchButton.setOnClickListener(view -> performSearch());

        loadCategoriesIntoSpinner();

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                long selectedCategoryId = categoryIds.get(position);
                if (selectedCategoryId == 0) { // "All category" được chọn
                    loadRestaurants();
                } else {
                    loadRestaurantsByCategory(selectedCategoryId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì
            }
        });

        loadRestaurants();
    }

    private void ShowProfileOptions() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_profile_options);

        Button viewReviewHistory = dialog.findViewById(R.id.view_review_history);
        Button logout = dialog.findViewById(R.id.logout);

        viewReviewHistory.setOnClickListener(view -> {
            Intent intent = new Intent(activity_main.this, ReviewHistory.class);
            startActivity(intent);
            dialog.dismiss();
        });

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
                Intent intent = new Intent(activity_main.this, ManageCategories.class);
                startActivity(intent);
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

    private void loadCategoriesIntoSpinner() {
        Cursor cursor = dbHelper.getAllCategories();
        List<String> categories = new ArrayList<>();
        categories.add("All category"); // Thêm mục "All category"
        categoryIds = new SparseArray<>();

        categoryIds.put(0, 0L); // ID cho "All category"

        if (cursor != null && cursor.moveToFirst()) {
            int index = 1; // Bắt đầu từ 1 vì 0 là "All category"
            do {
                String categoryName = cursor.getString(cursor.getColumnIndexOrThrow("_name"));
                long categoryId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
                categories.add(categoryName);
                categoryIds.put(index++, categoryId); // Lưu ID tương ứng
            } while (cursor.moveToNext());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        if (cursor != null) {
            cursor.close();
        }
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

    private void loadRestaurantsByCategory(long categoryId) {
        Cursor cursor = dbHelper.getRestaurantsByCategory(categoryId);
        adapter = new RestaurantAdapter(this, cursor);
        recyclerView.setAdapter(adapter);
    }
}