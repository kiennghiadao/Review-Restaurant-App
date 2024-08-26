package com.example.nmobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class activity_login extends AppCompatActivity {

    public EditText etEmail;
    public EditText etPassword;
    public Button btnLogin;
    public TextView btnRegister;
    public TextView btnForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email1 = etEmail.getText().toString().trim();
                String Password1 = etPassword.getText().toString().trim();

                if (Email1.isEmpty() || Password1.isEmpty()) {
                    Toast.makeText(activity_login.this, "Please fill in all information!", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseHelper dbHelper = new DatabaseHelper(activity_login.this);
                    boolean isValidUser = dbHelper.checkUser(Email1, Password1);

                    if (isValidUser) {
                        String role = dbHelper.getUserRole(Email1);
                        int userId = dbHelper.getUserIdByEmail(Email1); // Lấy ID người dùng

                        // Lưu ID người dùng vào SharedPreferences
                        UserSessionManager sessionManager = new UserSessionManager(activity_login.this);
                        sessionManager.saveUserId(userId);

                        Toast.makeText(activity_login.this, "Log in successfully!", Toast.LENGTH_SHORT).show();
                        // Chuyển sang màn hình chính hoặc màn hình người dùng
                        Intent intent = new Intent(activity_login.this, activity_main.class);
                        intent.putExtra("User Role", role);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(activity_login.this, "Email or password is incorrect!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_login.this, activity_register.class);
                startActivity(intent);
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_login.this, activity_forgot_password.class);
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