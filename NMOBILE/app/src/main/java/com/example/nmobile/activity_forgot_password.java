package com.example.nmobile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class activity_forgot_password extends AppCompatActivity {

    public EditText etForgotEmail;
    public Button btnSendRequest;
    public DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        etForgotEmail = findViewById(R.id.etForgotEmail);
        btnSendRequest = findViewById(R.id.btnSendRequest);
        dbHelper = new DatabaseHelper(this);

        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etForgotEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(activity_forgot_password.this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
                } else {
                    if (dbHelper.checkEmailExists(email)) {
                        long requestTime = System.currentTimeMillis(); // Thời gian yêu cầu
                        String status = "Pending"; // Trạng thái yêu cầu

                        boolean result = dbHelper.addPasswordResetRequest(email, requestTime, status);
                        if (result) {
                            Toast.makeText(activity_forgot_password.this, "Yêu cầu đặt lại mật khẩu đã được gửi!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity_forgot_password.this, "Lỗi khi gửi yêu cầu đặt lại mật khẩu!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(activity_forgot_password.this, "Email không tồn tại trong hệ thống!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
