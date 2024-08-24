package com.example.nmobile;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.app.Dialog;
import android.widget.PopupMenu;
import android.widget.Toast;

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

        Button categoryButton = findViewById(R.id.category_button);
        categoryButton.setOnClickListener(view -> showCategoryMenu(view));

        loadRestaurants();
    }

    private void ShowProfileOptions() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_profile_options);

        // Set up common menu items
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

    private void showCategoryMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        popupMenu.getMenu().add("All category"); // Thêm mục "All category"

        Cursor cursor = null;
        try {
            // Lấy các danh mục từ cơ sở dữ liệu và thêm vào menu
            cursor = dbHelper.getAllCategories();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Đọc dữ liệu từ Cursor
                    String categoryName = cursor.getString(cursor.getColumnIndexOrThrow("_name")); // Cập nhật từ COLUMN_CATEGORY_NAME thành _id
                    long categoryId = cursor.getLong(cursor.getColumnIndexOrThrow("_id")); // Cập nhật từ COLUMN_CATEGORY_ID thành _id
                    popupMenu.getMenu().add(0, (int) categoryId, 0, categoryName);
                } while (cursor.moveToNext());
            } else {
                Toast.makeText(this, "No categories found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace(); // Ghi lại lỗi nếu có
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            int selectedCategoryId = item.getItemId();
            if (selectedCategoryId == 0) { // "All category" được chọn
                loadRestaurants();
            } else {
                loadRestaurantsByCategory(selectedCategoryId);
            }
            return true;
        });

        popupMenu.show();
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
