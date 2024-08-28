package com.example.nmobile;

import android.database.Cursor; // Nhập khẩu Cursor để làm việc với dữ liệu
import android.os.Bundle; // Nhập khẩu Bundle để lưu trữ trạng thái Activity
import android.view.MotionEvent; // Nhập khẩu MotionEvent để xử lý sự kiện chạm
import android.view.View; // Nhập khẩu View để quản lý giao diện
import android.widget.Button; // Nhập khẩu Button để sử dụng nút
import android.widget.Toast; // Nhập khẩu Toast để hiển thị thông báo ngắn

import androidx.annotation.NonNull; // Nhập khẩu annotation để kiểm tra null
import androidx.appcompat.app.AppCompatActivity; // Nhập khẩu AppCompatActivity để tạo Activity
import androidx.recyclerview.widget.LinearLayoutManager; // Nhập khẩu LinearLayoutManager để quản lý RecyclerView
import androidx.recyclerview.widget.RecyclerView; // Nhập khẩu RecyclerView để hiển thị danh sách

import java.util.HashSet; // Nhập khẩu HashSet để lưu trữ ID nhà hàng đã chọn
import java.util.Set; // Nhập khẩu Set để sử dụng tập hợp

public class AddRestaurantToCategory extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;// Biến RecyclerView để hiển thị danh sách nhà hàng
    private RestaurantAdapter adapter;// Adapter để kết nối dữ liệu với RecyclerView
    private long categoryId;// ID danh mục
    private Set<Long> selectedRestaurantIds = new HashSet<>();// Tập hợp ID nhà hàng đã chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_restaurant_to_category);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.restaurant_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));// Thiết lập LayoutManager

        // Lấy categoryId từ Intent
        categoryId = getIntent().getLongExtra("categoryId", -1);

        loadRestaurants();

        Button addSelectedButton = findViewById(R.id.add_selected_restaurants_button);
        addSelectedButton.setOnClickListener(v -> addSelectedRestaurantsToCategory());

        // Thiết lập sự kiện touch cho RecyclerView
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    View childView = rv.findChildViewUnder(e.getX(), e.getY());// Lấy view con theo tọa độ chạm
                    if (childView != null) {
                        int position = rv.getChildAdapterPosition(childView);// Lấy vị trí của view
                        Cursor cursor = adapter.getCursor();
                        if (cursor != null && cursor.moveToPosition(position)) {
                            // Lấy ID nhà hàng
                            long restaurantId = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESTAURANT_ID));
                            toggleSelection(restaurantId);// Chuyển đổi trạng thái chọn
                        }
                    }
                }
                return true;// Ngăn chặn sự kiện tiếp tục
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });
    }
    // Phương thức tải danh sách nhà hàng
    private void loadRestaurants() {
        Cursor cursor = dbHelper.getAllRestaurants();// Lấy cursor từ dbHelper
        adapter = new RestaurantAdapter(this, cursor);// Khởi tạo adapter với cursor
        recyclerView.setAdapter(adapter);// Thiết lập adapter cho RecyclerView
    }
    // Phương thức chuyển đổi trạng thái chọn
    private void toggleSelection(long restaurantId) {
        if (selectedRestaurantIds.contains(restaurantId)) {
            selectedRestaurantIds.remove(restaurantId);// Bỏ chọn nếu đã chọn
        } else {
            selectedRestaurantIds.add(restaurantId);// Chọn nếu chưa chọn
        }
    }
    // Phương thức thêm nhà hàng đã chọn vào danh mục
    private void addSelectedRestaurantsToCategory() {
        if (!selectedRestaurantIds.isEmpty()) {
            for (long restaurantId : selectedRestaurantIds) {
                // Thêm từng nhà hàng vào danh mục
                dbHelper.addRestaurantToCategory(categoryId, restaurantId);
            }
            Toast.makeText(this, "Added selected restaurants to category", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "No restaurants selected", Toast.LENGTH_SHORT).show();
        }
    }
}
