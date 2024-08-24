package com.example.nmobile;

import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ManageCategories extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText categoryNameInput;
    private ListView categoriesListView;
    private SimpleCursorAdapter adapter;
    private long selectedCategoryId = -1;

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

        addrestauranttocategory.setOnClickListener(v -> addRestaurantToCategory());
        addCategoryButton.setOnClickListener(v -> addCategory());
        updateCategoryButton.setOnClickListener(v -> updateCategory());
        deleteCategoryButton.setOnClickListener(v -> deleteCategory());

        categoriesListView.setOnItemClickListener((parent, view, position, id) -> {
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            // Lấy chỉ số cột `_id` và `_name`
            int columnIndexId = cursor.getColumnIndex("_id");
            int columnIndexName = cursor.getColumnIndex("_name");

            // Kiểm tra nếu các cột tồn tại và lấy dữ liệu
            if (columnIndexId != -1 && columnIndexName != -1) {
                selectedCategoryId = cursor.getLong(columnIndexId);
                categoryNameInput.setText(cursor.getString(columnIndexName));
            } else {
                Toast.makeText(this, "Column not found in Cursor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCategories() {
        // Lấy cursor từ dbHelper
        Cursor cursor = dbHelper.getAllCategories();

        // Thêm mục "All category" vào đầu cursor
        MatrixCursor extras = new MatrixCursor(new String[]{"_id", "_name"});
        extras.addRow(new Object[]{-1, "All category"});
        Cursor[] cursors = {extras, cursor};
        Cursor extendedCursor = new MergeCursor(cursors);

        // Đảm bảo tên cột khớp với alias
        String[] from = {"_name"};
        int[] to = {android.R.id.text1};

        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, extendedCursor, from, to, 0);
        categoriesListView.setAdapter(adapter);
    }

    private void addCategory() {
        String categoryName = categoryNameInput.getText().toString();
        if (!categoryName.isEmpty()) {
            if (dbHelper.addCategory(categoryName)) {
                Toast.makeText(this, "Add category successfully!", Toast.LENGTH_SHORT).show();
                loadCategories();
                categoryNameInput.setText("");
            } else {
                Toast.makeText(this, "Add category failed!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Category name cannot be empty!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCategory() {
        String categoryName = categoryNameInput.getText().toString();
        if (selectedCategoryId != -1 && !categoryName.isEmpty()) {
            if (dbHelper.updateCategory(selectedCategoryId, categoryName)) {
                Toast.makeText(this, "Update category successfully!", Toast.LENGTH_SHORT).show();
                loadCategories();
                categoryNameInput.setText("");
                selectedCategoryId = -1;
            } else {
                Toast.makeText(this, "Update category failed!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Select a category to update!", Toast.LENGTH_SHORT).show();
        }
    }

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
