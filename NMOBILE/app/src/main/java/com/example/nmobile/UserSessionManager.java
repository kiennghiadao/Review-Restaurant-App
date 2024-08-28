package com.example.nmobile;

import android.content.Context;
import android.content.SharedPreferences; // SharedPreferences để lưu trữ dữ liệu

public class UserSessionManager {

    private static final String PREF_NAME = "UserSession";// Tên của tệp SharedPreferences
    private static final String KEY_USER_ID = "user_id";// Khóa để lưu trữ ID người dùng
    private SharedPreferences sharedPreferences;// Biến để lưu trữ đối tượng SharedPreferences
    private SharedPreferences.Editor editor;// Biến để lưu trữ đối tượng Editor

    // Constructor để khởi tạo UserSessionManager
    public UserSessionManager(Context context) {
        //Khởi tạo SharedPreferences
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        // Khởi tạo Editor để chỉnh sửa SharedPreferences
        editor = sharedPreferences.edit();
    }
    // Phương thức để lưu ID người dùng
    public void saveUserId(int userId) {
        editor.putInt(KEY_USER_ID, userId);// Lưu ID người dùng vào SharedPreferences
        editor.apply();// Áp dụng thay đổi
    }
    // Phương thức để lấy ID người dùng
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1); // Trả về ID người dùng, -1 nếu không tìm thấy
    }
}
