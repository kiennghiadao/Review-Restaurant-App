package com.example.nmobile;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;
import android.app.AlertDialog;

public class AddRestaurantActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText locationEditText;
    private EditText typeEditText;
    private EditText detailsEditText;
    private RatingBar ratingBar;
    private ImageView imageView;
    private Button addButton;
    private Button chooseImageButton;

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

        // Xử lý sự kiện nút chọn hình ảnh
        chooseImageButton.setOnClickListener(v -> showImagePicker());

        // Xử lý sự kiện nút thêm nhà hàng
        addButton.setOnClickListener(v -> addRestaurant());
    }

    private void showImagePicker() {
        List<String> imageNames = getImageNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, imageNames);

        new AlertDialog.Builder(this)
                .setTitle("Chọn hình ảnh")
                .setAdapter(adapter, (dialog, which) -> {
                    String selectedImageName = imageNames.get(which);
                    selectedImageResId = getResources().getIdentifier(selectedImageName, "drawable", getPackageName());
                    imageView.setImageResource(selectedImageResId);
                })
                .show();
    }

    private List<String> getImageNames() {
        List<String> imageNames = new ArrayList<>();
        Field[] drawables = R.drawable.class.getDeclaredFields();
        for (Field field : drawables) {
            if (field.getName().startsWith("image_")) {
                imageNames.add(field.getName());
            }
        }
        return imageNames;
    }

    private void addRestaurant() {
        String name = nameEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String type = typeEditText.getText().toString();
        String details = detailsEditText.getText().toString();
        float rating = ratingBar.getRating();

        // Khởi tạo DatabaseHelper và ContentValues
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_RESTAURANT_NAME, name);
        contentValues.put(DatabaseHelper.COLUMN_LOCATION, location);
        contentValues.put(DatabaseHelper.COLUMN_TYPE, type);
        contentValues.put(DatabaseHelper.COLUMN_DETAILS, details);
        contentValues.put(DatabaseHelper.COLUMN_RESTAURANT_RATING, rating);
        contentValues.put(DatabaseHelper.COLUMN_IMAGE_URL, selectedImageResId);

        // Thêm nhà hàng vào cơ sở dữ liệu
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long result = db.insert(DatabaseHelper.TABLE_RESTAURANTS, null, contentValues);

        if (result != -1) {
            // Hiển thị thông báo thành công và đóng Activity
            Toast.makeText(this, "Restaurant added successfully", Toast.LENGTH_SHORT).show();
            finish(); // Đóng AddRestaurantActivity và quay lại activity trước
        } else {
            // Hiển thị thông báo lỗi
            Toast.makeText(this, "Error adding restaurant", Toast.LENGTH_SHORT).show();
        }
    }
}
