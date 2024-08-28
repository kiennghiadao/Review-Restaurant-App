package com.example.nmobile;

import android.content.Intent; // Nhập khẩu Intent để chuyển đổi Activity
import android.database.Cursor; // Nhập khẩu Cursor để làm việc với dữ liệu
import android.database.MatrixCursor; // Nhập khẩu MatrixCursor để tạo cursor tạm
import android.database.MergeCursor; // Nhập khẩu MergeCursor để kết hợp nhiều cursor
import android.os.Bundle; // Nhập khẩu Bundle để lưu trữ trạng thái Activity
import android.widget.Button; // Nhập khẩu Button để sử dụng nút
import android.widget.EditText; // Nhập khẩu EditText để nhận input từ người dùng
import android.widget.ListView; // Nhập khẩu ListView để hiển thị danh sách
import android.widget.SimpleCursorAdapter; // Nhập khẩu SimpleCursorAdapter để kết nối dữ liệu với ListView
import android.widget.Toast; // Nhập khẩu Toast để hiển thị thông báo ngắn

import androidx.appcompat.app.AppCompatActivity;

public class ManageCategories extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText categoryNameInput;
    private ListView categoriesListView;// Biến ListView để hiển thị danh sách danh mục
    private SimpleCursorAdapter adapter;// Adapter để kết nối dữ liệu với ListView
    private long selectedCategoryId = -1;// ID danh mục được chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_categories);

        dbHelper = new DatabaseHelper(this);

        categoryNameInput = findViewById(R.id.category_name_input);
        categoriesListView = findViewById(R.id.categories_list_view);
        Button addCategoryButton = findViewById(R.id.add_category_button);
        Button updateCategoryButton = findViewById(R.id.update_category_button);
        Button deleteCategoryButton = findViewById(R.id.delete_category_button);
        Button addrestauranttocategory = findViewById(R.id.add_restaurant_to_category);
        loadCategories();

        // Thiết lập sự kiện click cho các nút
        addrestauranttocategory.setOnClickListener(v -> addRestaurantToCategory());
        addCategoryButton.setOnClickListener(v -> addCategory());
        updateCategoryButton.setOnClickListener(v -> updateCategory());
        deleteCategoryButton.setOnClickListener(v -> deleteCategory());

        // Thiết lập sự kiện click cho mỗi item trong danh sách
        categoriesListView.setOnItemClickListener((parent, view, position, id) -> {
            // Lấy cursor từ vị trí click
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            // Lấy chỉ số cột `_id` và `_name`
            int columnIndexId = cursor.getColumnIndex("_id");
            int columnIndexName = cursor.getColumnIndex("_name");

            // Kiểm tra nếu các cột tồn tại và lấy dữ liệu
            if (columnIndexId != -1 && columnIndexName != -1) {
                // Lưu ID danh mục được chọn
                selectedCategoryId = cursor.getLong(columnIndexId);
                // Hiển thị tên danh mục
                categoryNameInput.setText(cursor.getString(columnIndexName));
            } else {
                Toast.makeText(this, "Column not found in Cursor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức tải danh sách danh mục từ cơ sở dữ liệu
    private void loadCategories() {
        // Lấy cursor từ dbHelper
        Cursor cursor = dbHelper.getAllCategories();

        // Thêm mục "All category" vào đầu cursor
        MatrixCursor extras = new MatrixCursor(new String[]{"_id", "_name"});
        extras.addRow(new Object[]{-1, "All category"});
        Cursor[] cursors = {extras, cursor};
        Cursor extendedCursor = new MergeCursor(cursors);// Kết hợp các cursor

        // Đảm bảo tên cột khớp với alias
        String[] from = {"_name"};// Tên cột
        int[] to = {android.R.id.text1};// ID view để ánh xạ

        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, extendedCursor, from, to, 0);
        categoriesListView.setAdapter(adapter);// Thiết lập adapter cho ListView
    }
    // Phương thức thêm danh mục
    private void addCategory() {
        String categoryName = categoryNameInput.getText().toString();// Lấy tên danh mục từ input
        if (!categoryName.isEmpty()) {
            if (dbHelper.addCategory(categoryName)) {// Thêm danh mục vào cơ sở dữ liệu
                Toast.makeText(this, "Add category successfully!", Toast.LENGTH_SHORT).show();
                loadCategories();// Tải lại danh sách danh mục
                categoryNameInput.setText("");// Xóa input
            } else {
                Toast.makeText(this, "Add category failed!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Category name cannot be empty!", Toast.LENGTH_SHORT).show();
        }
    }
    // Phương thức cập nhật danh mục
    private void updateCategory() {
        String categoryName = categoryNameInput.getText().toString();// Lấy tên danh mục từ input
        if (selectedCategoryId != -1 && !categoryName.isEmpty()) {
            if (dbHelper.updateCategory(selectedCategoryId, categoryName)) {// Cập nhật danh mục
                Toast.makeText(this, "Update category successfully!", Toast.LENGTH_SHORT).show();
                loadCategories();// Tải lại danh sách danh mục
                categoryNameInput.setText("");// Xóa input
                selectedCategoryId = -1;// Reset ID đã chọn
            } else {
                Toast.makeText(this, "Update category failed!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Select a category to update!", Toast.LENGTH_SHORT).show();
        }
    }

    // Phương thức xóa danh mục
    private void deleteCategory() {
        if (selectedCategoryId != -1) {
            if (dbHelper.deleteCategory(selectedCategoryId)) {
                Toast.makeText(this, "Delete category successfully!", Toast.LENGTH_SHORT).show();
                loadCategories();
                categoryNameInput.setText("");
                selectedCategoryId = -1;
            } else {
                Toast.makeText(this, "Delete category failed!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Select the category to delete!", Toast.LENGTH_SHORT).show();
        }
    }

    // Phương thức thêm nhà hàng vào danh mục
    private void addRestaurantToCategory() {
        if (selectedCategoryId != -1) {
            // Mở AddRestaurantToCategoryActivity và truyền categoryId đã chọn
            Intent intent = new Intent(this, AddRestaurantToCategory.class);
            intent.putExtra("categoryId", selectedCategoryId);
            startActivity(intent);
        } else {
            // Thông báo nếu chưa chọn category
            Toast.makeText(this, "Please select a category first!", Toast.LENGTH_SHORT).show();
        }
    }
}
