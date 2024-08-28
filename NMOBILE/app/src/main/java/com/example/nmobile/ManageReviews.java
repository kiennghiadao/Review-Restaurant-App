package com.example.nmobile;

import android.content.Intent; // Nhập khẩu Intent để chuyển đổi Activity
import android.database.Cursor; // Nhập khẩu Cursor để làm việc với dữ liệu
import android.database.sqlite.SQLiteDatabase; // Nhập khẩu SQLiteDatabase để truy cập cơ sở dữ liệu
import android.os.Bundle; // Nhập khẩu Bundle để lưu trữ trạng thái Activity
import android.widget.ListView; // Nhập khẩu ListView để hiển thị danh sách
import android.widget.SimpleAdapter; // Nhập khẩu SimpleAdapter để dễ dàng kết nối dữ liệu với ListView
import androidx.appcompat.app.AppCompatActivity; // Nhập khẩu AppCompatActivity để tạo Activity

import java.util.ArrayList; // Nhập khẩu ArrayList để sử dụng danh sách động
import java.util.HashMap; // Nhập khẩu HashMap để lưu trữ cặp khóa-giá trị
import java.util.List; // Nhập khẩu List để sử dụng danh sách
import java.util.Map; // Nhập khẩu Map để lưu trữ dữ liệu dạng bản đồ

public class ManageReviews extends AppCompatActivity {

    private ListView restaurantListView;// Biến ListView để hiển thị danh sách nhà hàng
    private List<Map<String, Object>> restaurantData;// Danh sách chứa dữ liệu nhà hàng
    private static final String[] FROM = {"name", "image"};// Khóa để ánh xạ dữ liệu
    private static final int[] TO = {R.id.restaurant_name, R.id.restaurant_image};// ID view để ánh xạ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_reviews);

        restaurantListView = findViewById(R.id.restaurantListView);
        restaurantData = new ArrayList<>();

        loadRestaurantList();
        // Tạo SimpleAdapter để kết nối dữ liệu với ListView
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                restaurantData,
                R.layout.restaurant_item, // Layout cho mỗi item
                FROM,
                TO
        );

        restaurantListView.setAdapter(adapter); // Thiết lập adapter cho ListView

        // Thiết lập sự kiện click cho mỗi item trong danh sách
        restaurantListView.setOnItemClickListener((adapterView, view, position, id) -> {
            int restaurantId = (int) restaurantData.get(position).get("id");// Lấy ID nhà hàng
            Intent intent = new Intent(ManageReviews.this, RestaurantComments.class);
            intent.putExtra("restaurant_id", restaurantId); // Gửi ID nhà hàng
            startActivity(intent);
        });
    }

    // Phương thức tải danh sách nhà hàng từ cơ sở dữ liệu
    private void loadRestaurantList() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.getAllRestaurants();// Lấy tất cả nhà hàng

        // Duyệt qua cursor để lấy dữ liệu
        while (cursor.moveToNext()) {
            int restaurantId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESTAURANT_ID));
            String restaurantName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESTAURANT_NAME));
            int restaurantImage = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE_URL));

            // Tạo bản đồ để lưu trữ dữ liệu nhà hàng
            Map<String, Object> data = new HashMap<>();
            data.put("id", restaurantId);
            data.put("name", restaurantName);
            data.put("image", restaurantImage);

            restaurantData.add(data);// Thêm dữ liệu vào danh sách
        }
        cursor.close();// Đóng cursor
    }
}
