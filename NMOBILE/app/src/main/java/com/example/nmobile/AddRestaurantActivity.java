package com.example.nmobile;

import android.content.ContentValues; // Để xử lý giá trị nội dung
import android.database.sqlite.SQLiteDatabase; // Để thực hiện các thao tác với cơ sở dữ liệu
import android.os.Bundle; // Để xử lý dữ liệu gói
import android.widget.ArrayAdapter; // Để sử dụng bộ điều hợp mảng
import android.widget.Button; // Để tạo nút
import android.widget.EditText; // Để nhập văn bản
import android.widget.ImageView; // Để hiển thị hình ảnh
import android.widget.RatingBar; // Để nhập đánh giá
import android.widget.Toast; // Để hiển thị thông báo ngắn
import androidx.appcompat.app.AlertDialog; // Để hiển thị hộp thoại cảnh báo
import androidx.appcompat.app.AppCompatActivity; // Lớp cơ sở cho hoạt động
import java.lang.reflect.Field; // Để truy cập tài nguyên drawable
import java.util.ArrayList; // Để sử dụng ArrayList
import java.util.List; // Để sử dụng List

public class AddRestaurantActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText locationEditText;
    private EditText typeEditText;
    private EditText detailsEditText;
    private RatingBar ratingBar;
    private ImageView imageView;
    private Button addButton;
    private Button chooseImageButton;
    private Button deleteButton;
    private Button updateButton;
    private int selectedImageResId = -1; // Khởi tạo với giá trị mặc định để tránh lỗi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);

        // Ánh xạ các phần tử giao diện
        nameEditText = findViewById(R.id.editTextName);
        locationEditText = findViewById(R.id.editTextLocation);
        typeEditText = findViewById(R.id.editTextType);
        detailsEditText = findViewById(R.id.editTextDetails);
        ratingBar = findViewById(R.id.ratingBar);
        imageView = findViewById(R.id.imageView);
        addButton = findViewById(R.id.buttonAddRestaurant);
        chooseImageButton = findViewById(R.id.buttonAddImage);
        deleteButton = findViewById(R.id.buttonDeleteRestaurant);
        updateButton = findViewById(R.id.buttonUpdateRestaurant);

        // Xử lý sự kiện nút chọn hình ảnh
        chooseImageButton.setOnClickListener(v -> showImagePicker());

        // Xử lý sự kiện nút xóa nhà hàng
        deleteButton.setOnClickListener(v -> deleteRestaurant());

        // Xử lý sự kiện nút thêm nhà hàng
        addButton.setOnClickListener(v -> addRestaurant());

        // Xử lý sự kiện nút chỉnh sửa nhà hàng
        updateButton.setOnClickListener(v -> updateRestaurant());
    }

    //phương thức xóa nhà hàng
    private void deleteRestaurant() {
        String name = nameEditText.getText().toString();// Lấy tên nhà hàng từ trường nhập

        DatabaseHelper dbHelper = new DatabaseHelper(this);// Tạo đối tượng DatabaseHelper
        SQLiteDatabase db = dbHelper.getWritableDatabase();// Mở cơ sở dữ liệu để ghi

        // Xóa nhà hàng theo tên
        int rowsDeleted = db.delete(DatabaseHelper.TABLE_RESTAURANTS, DatabaseHelper.COLUMN_RESTAURANT_NAME + "=?", new String[]{name});

        // Hiển thị thông báo kết quả
        if (rowsDeleted > 0) {
            Toast.makeText(this, "Restaurant deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Can't find restaurant", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }

    // Phương thức cập nhật thông tin nhà hàng
    private void updateRestaurant() {
        String name = nameEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String type = typeEditText.getText().toString();
        String details = detailsEditText.getText().toString();
        float rating = ratingBar.getRating();

        // Khởi tạo DatabaseHelper và ContentValues
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        ContentValues contentValues = new ContentValues();
        // Cập nhật các trường nếu không trống
        if (!location.isEmpty()) contentValues.put(DatabaseHelper.COLUMN_LOCATION, location);
        if (!type.isEmpty()) contentValues.put(DatabaseHelper.COLUMN_TYPE, type);
        if (!details.isEmpty()) contentValues.put(DatabaseHelper.COLUMN_DETAILS, details);
        if (rating >= 0) contentValues.put(DatabaseHelper.COLUMN_RESTAURANT_RATING, rating);
        if (selectedImageResId != -1) contentValues.put(DatabaseHelper.COLUMN_IMAGE_URL, selectedImageResId);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Cập nhật thông tin nhà hàng
        int rowsAffected = db.update(DatabaseHelper.TABLE_RESTAURANTS, contentValues,
                DatabaseHelper.COLUMN_RESTAURANT_NAME + "=?", new String[]{name});

        // Hiển thị thông báo kết quả
        if (rowsAffected > 0) {
            Toast.makeText(this, "Restaurant updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error updating restaurant", Toast.LENGTH_SHORT).show();
        }
    }

    // Phương thức hiển thị hộp thoại chọn hình ảnh
    private void showImagePicker() {
        List<String> imageNames = getImageNames();
        // Tạo bộ điều hợp để hiển thị tên hình ảnh
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, imageNames);

        // Tạo hộp thoại
        new AlertDialog.Builder(this)
                .setTitle("Chọn hình ảnh")
                .setAdapter(adapter, (dialog, which) -> {// Thiết lập bộ điều hợp và sự kiện khi chọn
                    String selectedImageName = imageNames.get(which);// Lấy tên hình ảnh đã chọn
                    // Lấy ID của hình ảnh
                    selectedImageResId = getResources().getIdentifier(selectedImageName, "drawable", getPackageName());
                    // Hiển thị hình ảnh đã chọn
                    imageView.setImageResource(selectedImageResId);
                })
                .show();
    }

    // Phương thức lấy tên các hình ảnh
    private List<String> getImageNames() {
        List<String> imageNames = new ArrayList<>();// Tạo danh sách để lưu tên hình ảnh
        Field[] drawables = R.drawable.class.getDeclaredFields();// Lấy tất cả các trường trong thư viện drawable
        for (Field field : drawables) {// Duyệt qua từng trường
            // Kiểm tra nếu tên trường bắt đầu bằng "image_"
            if (field.getName().startsWith("image_")) {
                imageNames.add(field.getName());// Thêm tên hình ảnh vào danh sách
            }
        }
        return imageNames;// Trả về danh sách tên hình ảnh
    }

    // Phương thức thêm nhà hàng mới
    private void addRestaurant() {
        String name = nameEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String type = typeEditText.getText().toString();
        String details = detailsEditText.getText().toString();
        float rating = ratingBar.getRating();

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_RESTAURANT_NAME, name);
        contentValues.put(DatabaseHelper.COLUMN_LOCATION, location);
        contentValues.put(DatabaseHelper.COLUMN_TYPE, type);
        contentValues.put(DatabaseHelper.COLUMN_DETAILS, details);
        contentValues.put(DatabaseHelper.COLUMN_RESTAURANT_RATING, rating);
        contentValues.put(DatabaseHelper.COLUMN_IMAGE_URL, selectedImageResId);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Thêm nhà hàng vào cơ sở dữ liệu
        long result = db.insert(DatabaseHelper.TABLE_RESTAURANTS, null, contentValues);

        // Hiển thị thông báo kết quả
        if (result != -1) {
            Toast.makeText(this, "Restaurant added successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error adding restaurant", Toast.LENGTH_SHORT).show();
        }
    }
}
