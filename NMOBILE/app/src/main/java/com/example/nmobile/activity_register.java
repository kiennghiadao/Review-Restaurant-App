package com.example.nmobile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class activity_register extends AppCompatActivity {

    public EditText etEmail;
    public EditText etPassword;
    public EditText etConfirmPassword;
    public Button btnRegister;
    public DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        etEmail=findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        dbHelper= new DatabaseHelper(this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmpassword = etConfirmPassword.getText().toString().trim();
                String role = "user"; // Thiết lập vai trò mặc định là user

                if(email.isEmpty() || password.isEmpty() || confirmpassword.isEmpty()) {
                    Toast.makeText(activity_register.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                } else if(!password.equals(confirmpassword)) {
                    Toast.makeText(activity_register.this, "Mật khẩu và xác nhận mật khẩu phải khớp nhau!", Toast.LENGTH_SHORT).show();
                } else {
                    if(dbHelper.checkUser(email)) {
                        Toast.makeText(activity_register.this, "Email đã tồn tại!", Toast.LENGTH_SHORT).show();
                    } else {
                        boolean result = dbHelper.addUser(email, password, role);
                        if(result) {
                            Toast.makeText(activity_register.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}