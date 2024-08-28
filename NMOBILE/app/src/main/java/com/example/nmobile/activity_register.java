package com.example.nmobile;

import android.os.Bundle; // Nhập thư viện để xử lý Bundle
import android.widget.Button; // Nhập thư viện để tạo nút
import android.widget.EditText; // Nhập thư viện để xử lý nhập văn bản
import android.view.View; // Nhập thư viện để sử dụng View
import android.widget.TextView; // Nhập thư viện để hiển thị văn bản
import android.widget.Toast; // Nhập thư viện để hiển thị thông báo ngắn
import android.content.Intent; // Nhập thư viện để xử lý Intent

import androidx.activity.EdgeToEdge; // Nhập thư viện cho tính năng EdgeToEdge
import androidx.appcompat.app.AppCompatActivity; // Nhập thư viện để sử dụng lớp hoạt động cơ bản
import androidx.core.graphics.Insets; // Nhập thư viện để xử lý Insets
import androidx.core.view.ViewCompat; // Nhập thư viện để xử lý ViewCompat
import androidx.core.view.WindowInsetsCompat; // Nhập thư viện để xử lý WindowInsets

public class activity_register extends AppCompatActivity {

    public EditText etEmail;
    public EditText etPassword;
    public EditText etConfirmPassword;
    public Button btnRegister;
    public DatabaseHelper dbHelper;
    public TextView tvSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        tvSignIn = findViewById(R.id.tvSignIn);
        etEmail=findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        dbHelper= new DatabaseHelper(this);

        // Thiết lập sự kiện khi nhấn nút đăng ký
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString().trim();// Lấy email và loại bỏ khoảng trắng
                String password = etPassword.getText().toString().trim();
                String confirmpassword = etConfirmPassword.getText().toString().trim();
                String role = "user"; // Thiết lập vai trò mặc định là user

                // Kiểm tra thông tin nhập vào
                if(email.isEmpty() || password.isEmpty() || confirmpassword.isEmpty()) {
                    Toast.makeText(activity_register.this, "Please fill in all information!", Toast.LENGTH_SHORT).show();
                } else if(!password.equals(confirmpassword)) {
                    Toast.makeText(activity_register.this, "Password and password confirmation must match!", Toast.LENGTH_SHORT).show();
                } else {
                    if(dbHelper.checkUser(email)) {
                        Toast.makeText(activity_register.this, "Email already exists!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Thêm người dùng vào cơ sở dữ liệu
                        boolean result = dbHelper.addUser(email, password, role);
                        if(result) {
                            Toast.makeText(activity_register.this, "Registered successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity_register.this, activity_login.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(activity_register.this, "Đăng ký thất bại, hãy thử lại sau!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        // Thiết lập sự kiện khi nhấn vào tvSignIn
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_register.this, activity_login.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}