package com.example.nmobile;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Manage_Users extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText emailInput, passwordInput, roleInput;
    private ListView requestsListView, UserListView;
    private Button addButton, editButton, deleteButton, requestPasswordButton, viewUserButtonList;
    private boolean isRequestsListVisiblePass = false; // Biến trạng thái để theo dõi hiển thị danh sách yêu cầu
    private boolean isRequestsListVisibleUser = false; // Biến trạng thái để theo dõi hiển thị danh sách yêu cầu
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        dbHelper = new DatabaseHelper(this);

        // Ánh xạ các view
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        roleInput = findViewById(R.id.role_input);
        requestsListView = findViewById(R.id.requests_list_view);
        UserListView = findViewById(R.id.users_list_view);
        addButton = findViewById(R.id.add_user_button);
        editButton = findViewById(R.id.edit_user_button);
        deleteButton = findViewById(R.id.delete_user_button);
        requestPasswordButton = findViewById(R.id.request_password_button);
        viewUserButtonList = findViewById(R.id.view_user_button);

        // Thiết lập sự kiện cho các nút
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editUser();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUser();
            }
        });

        requestPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               togglePasswordResetRequests();
            }
        });

        viewUserButtonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleUserList();
            }
        });
    }

    // Hàm hiển thị danh sách người dùng trong giao diện quản lý user
    private void displayUsers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id AS _id, email, role FROM users", null);

        String[] fromColumns = { "email", "role" };
        int[] toViews = { R.id.user_email, R.id.user_role };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.user_item, // XML layout cho từng item trong danh sách
                cursor,
                fromColumns,
                toViews,
                0
        );

        UserListView.setAdapter(adapter);
    }

    // Hàm hiển thị yêu cầu cấp lại mật khẩu
    private void displayPasswordResetRequests() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT request_id AS _id, email, request_time, status FROM password_reset_requests", null);

        String[] fromColumns = { "email", "request_time", "status" };
        int[] toViews = { R.id.request_email, R.id.request_timestamp, R.id.request_status };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.password_reset_request_item,
                cursor,
                fromColumns,
                toViews,
                0
        );
        requestsListView.setAdapter(adapter);
    }

    // Hàm add user
    private void addUser() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String role = roleInput.getText().toString();

        if (email.isEmpty() || password.isEmpty() || role.isEmpty()) {
            Toast.makeText(this, "Please fill in all information!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.checkEmailExists(email)) {
            Toast.makeText(this, "Email already exists!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean result = dbHelper.addManageUser(email, password, role);
        if (result) {
            Toast.makeText(this, "User added successfully!", Toast.LENGTH_SHORT).show();
            clearInputFields();
            displayUsers();
        } else {
            Toast.makeText(this, "Adding user failed!", Toast.LENGTH_SHORT).show();
        }
    }

    // Hàm edit user
    private void editUser() {
        String email = emailInput.getText().toString().trim();
        String newPassword = passwordInput.getText().toString().trim();
        String newRole = roleInput.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Enter the email of the user that needs to be edited!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.isEmpty() && newRole.isEmpty()) {
            Toast.makeText(this, "Please fill out at least one information!", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = dbHelper.getUserIdByEmail(email);
        if (userId == -1) {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean result = dbHelper.editUser(userId, newPassword, newRole);
        if (result) {
            boolean deleteResult = dbHelper.deletePasswordResetRequest(email);
            if (deleteResult) {
                Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
            }
            clearInputFields(); // Xóa các trường nhập liệu
            Toast.makeText(this, "User edited successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "User editing failed!", Toast.LENGTH_SHORT).show();
        }
    }

    // Hàm delete user
    private void deleteUser() {
        String email = emailInput.getText().toString();

        if (email.isEmpty()) {
            Toast.makeText(this, "Enter the email of the user to be deleted!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean exists = dbHelper.checkEmailExists(email);
        if (!exists) {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_USERS, DatabaseHelper.COLUMN_EMAIL + "=?", new String[]{email});
        Toast.makeText(this, "User deletion successful!", Toast.LENGTH_SHORT).show();
        clearInputFields();
        displayUsers();
    }

    // Xóa các trường nhập liệu của user sau khi add, edit, delete
    private void clearInputFields() {
        emailInput.setText("");
        passwordInput.setText("");
        roleInput.setText("");
    }

    // Hàm hiển thị hoặc ẩn yêu cầu cấp lại mật khẩu
    private void togglePasswordResetRequests() {
        if (isRequestsListVisiblePass) {
            // Ẩn danh sách yêu cầu
            requestsListView.setVisibility(View.GONE);
            isRequestsListVisiblePass = false;
        } else {
            // Hiển thị danh sách yêu cầu
            displayPasswordResetRequests();
            requestsListView.setVisibility(View.VISIBLE);
            isRequestsListVisiblePass = true;
        }
    }

    // Hiển thị danh sách người dùng
    private void toggleUserList() {
        if (isRequestsListVisibleUser) {
            // Ẩn danh sách yêu cầu
            UserListView.setVisibility(View.GONE);
            isRequestsListVisibleUser = false;
        } else {
            // Hiển thị danh sách yêu cầu
            displayUsers();
            UserListView.setVisibility(View.VISIBLE);
            isRequestsListVisibleUser = true;
        }
    }
}
