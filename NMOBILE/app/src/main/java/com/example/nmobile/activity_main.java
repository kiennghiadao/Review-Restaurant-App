package com.example.nmobile;

import android.content.Intent; // Để xử lý các intent
import android.database.Cursor; // Để truy vấn kết quả từ cơ sở dữ liệu
import android.os.Bundle; // Để truyền dữ liệu giữa các hoạt động
import android.view.View; // Để xử lý sự kiện từ view
import android.widget.ArrayAdapter; // Để tạo danh sách dropdown
import android.widget.Button; // Để tạo nút
import android.widget.EditText; // Để nhập văn bản
import android.widget.ImageButton; // Để tạo nút hình ảnh
import android.app.Dialog; // Để tạo cửa sổ đối thoại
import android.widget.AdapterView; // Để xử lý sự kiện khi chọn item trong spinner
import android.widget.Spinner; // Để tạo danh sách dropdown

import androidx.appcompat.app.AppCompatActivity; // Lớp cơ sở cho các hoạt động
import androidx.recyclerview.widget.LinearLayoutManager; // Để thiết lập layout cho RecyclerView
import androidx.recyclerview.widget.RecyclerView; // Để hiển thị danh sách

import java.util.ArrayList; // Để sử dụng ArrayList
import java.util.List; // Để xử lý danh sách
import android.util.SparseArray; // Để ánh xạ khóa với giá trị

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

        // Ánh xạ các phần tử giao diện
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

        // Thiết lập sự kiện cho spinner
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                long selectedCategoryId = categoryIds.get(position);// Lấy ID danh mục đã chọn
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
        loadCategoriesIntoSpinner();
    }

    // Hiển thị tùy chọn hồ sơ
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

    // Tải danh mục vào spinner
    private void loadCategoriesIntoSpinner() {
        Cursor cursor = dbHelper.getAllCategories();// Lấy tất cả danh mục
        List<String> categories = new ArrayList<>();//Tạo một danh sách mới để lưu tên các danh mục.
        categories.add("All category"); // Thêm mục "All category"
        categoryIds = new SparseArray<>();//Tạo một SparseArray để ánh xạ vị trí của danh mục tới ID của nó.

        categoryIds.put(0, 0L); // ID cho "All category"

        //Đảm bảo rằng con trỏ không null và đang trỏ tới hàng đầu tiên trong kết quả truy vấn.
        if (cursor != null && cursor.moveToFirst()) {
            int index = 1; // Bắt đầu từ 1 vì 0 là "All category"
            do {
                //Lấy giá trị tên danh mục từ cột _name và _id
                String categoryName = cursor.getString(cursor.getColumnIndexOrThrow("_name"));
                long categoryId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
                categories.add(categoryName);//Thêm tên danh mục vừa lấy được vào danh sách categories.
                categoryIds.put(index++, categoryId); // Lưu ID tương ứng
            } while (cursor.moveToNext());//Tiếp tục vòng lặp cho đến khi không còn hàng nào trong con trỏ.
        }

        //Tạo một adapter để kết nối danh sách categories với Spinner.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        //Chỉ định layout cho các item hiển thị khi dropdown mở.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);// Gán adapter cho spinner

        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRestaurants();
        loadCategoriesIntoSpinner();
    }

    // Thực hiện tìm kiếm
    private void performSearch() {
        String query = searchBar.getText().toString();// Lấy truy vấn từ thanh tìm kiếm
        Cursor cursor = dbHelper.searchRestaurants(query);// Tìm kiếm nhà hàng
        adapter = new RestaurantAdapter(this, cursor);// Tạo adapter với kết quả tìm kiếm
        recyclerView.setAdapter(adapter);// Thiết lập adapter cho RecyclerView
    }
    // Tải tất cả nhà hàng
    private void loadRestaurants() {
        Cursor cursor = dbHelper.getAllRestaurants();// Lấy tất cả nhà hàng
        adapter = new RestaurantAdapter(this, cursor);
        recyclerView.setAdapter(adapter);
    }
    // Tải nhà hàng theo danh mục
    private void loadRestaurantsByCategory(long categoryId) {
        Cursor cursor = dbHelper.getRestaurantsByCategory(categoryId);// Lấy nhà hàng theo danh mục
        adapter = new RestaurantAdapter(this, cursor);
        recyclerView.setAdapter(adapter);
    }
}