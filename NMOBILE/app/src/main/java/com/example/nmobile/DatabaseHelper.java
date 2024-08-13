package com.example.nmobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "UserDB.db";
    private static final int DATABASE_VERSION = 5; // Tăng phiên bản cơ sở dữ liệu

    // Bảng users
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role";

    // Bảng restaurants
    public static final String TABLE_RESTAURANTS = "restaurants";
    public static final String COLUMN_RESTAURANT_ID = "restaurant_id";
    public static final String COLUMN_RESTAURANT_NAME = "name";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_RESTAURANT_RATING = "rating";
    public static final String COLUMN_DETAILS = "details";
    public static final String COLUMN_IMAGE_URL = "image_url"; // Cột mới

    // Bảng categories
    private static final String TABLE_CATEGORIES = "categories";
    private static final String COLUMN_CATEGORY_ID = "category_id";
    private static final String COLUMN_CATEGORY_NAME = "name";

    // Bảng reviews
    private static final String TABLE_REVIEWS = "reviews";
    private static final String COLUMN_REVIEW_ID = "review_id";
    private static final String COLUMN_REVIEW_USER_ID = "user_id";
    private static final String COLUMN_REVIEW_RESTAURANT_ID = "restaurant_id";
    private static final String COLUMN_REVIEW_RATING = "rating";
    private static final String COLUMN_COMMENTS = "comments";

    // Bảng comments
    private static final String TABLE_COMMENTS = "comments";
    private static final String COLUMN_COMMENT_ID = "comment_id";
    private static final String COLUMN_COMMENT_REVIEW_ID = "review_id";
    private static final String COLUMN_COMMENT_USER_ID = "user_id";
    private static final String COLUMN_CONTENT = "content";

    // Bảng yêu cầu đặt lại mật khẩu
    private static final String TABLE_PASSWORD_RESET_REQUESTS = "password_reset_requests";
    private static final String COLUMN_REQUEST_ID = "request_id";
    private static final String COLUMN_REQUEST_EMAIL = "email";
    private static final String COLUMN_REQUEST_TIME = "request_time"; // Đổi tên cột từ timestamp thành request_time
    private static final String COLUMN_REQUEST_STATUS = "status"; // "pending" hoặc "completed"

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng users
        String createUserTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_ROLE + " TEXT)";
        db.execSQL(createUserTable);

        // Thêm admin mặc định
        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_EMAIL, "admin@gmail.com");
        adminValues.put(COLUMN_PASSWORD, "admin123"); // Đảm bảo mã hóa mật khẩu trong thực tế
        adminValues.put(COLUMN_ROLE, "admin");
        db.insert(TABLE_USERS, null, adminValues);

        // Tạo bảng restaurants
        String createRestaurantTable = "CREATE TABLE " + TABLE_RESTAURANTS + " (" +
                COLUMN_RESTAURANT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RESTAURANT_NAME + " TEXT, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_TYPE + " TEXT, " +
                COLUMN_RESTAURANT_RATING + " REAL, " +
                COLUMN_DETAILS + " TEXT, " +
                COLUMN_IMAGE_URL + " INTEGER)";
        db.execSQL(createRestaurantTable);

        // Tạo bảng categories
        String createCategoryTable = "CREATE TABLE " + TABLE_CATEGORIES + " (" +
                COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CATEGORY_NAME + " TEXT)";
        db.execSQL(createCategoryTable);

        // Tạo bảng reviews
        String createReviewTable = "CREATE TABLE " + TABLE_REVIEWS + " (" +
                COLUMN_REVIEW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_REVIEW_USER_ID + " INTEGER, " +
                COLUMN_REVIEW_RESTAURANT_ID + " INTEGER, " +
                COLUMN_REVIEW_RATING + " REAL, " +
                COLUMN_COMMENTS + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_REVIEW_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "), " +
                "FOREIGN KEY(" + COLUMN_REVIEW_RESTAURANT_ID + ") REFERENCES " + TABLE_RESTAURANTS + "(" + COLUMN_RESTAURANT_ID + "))";
        db.execSQL(createReviewTable);

        // Tạo bảng comments
        String createCommentTable = "CREATE TABLE " + TABLE_COMMENTS + " (" +
                COLUMN_COMMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_COMMENT_REVIEW_ID + " INTEGER, " +
                COLUMN_COMMENT_USER_ID + " INTEGER, " +
                COLUMN_CONTENT + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_COMMENT_REVIEW_ID + ") REFERENCES " + TABLE_REVIEWS + "(" + COLUMN_REVIEW_ID + "), " +
                "FOREIGN KEY(" + COLUMN_COMMENT_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "))";
        db.execSQL(createCommentTable);

        // Tạo bảng yêu cầu đặt lại mật khẩu
        String createPasswordResetRequestTable = "CREATE TABLE " + TABLE_PASSWORD_RESET_REQUESTS + " (" +
                COLUMN_REQUEST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_REQUEST_EMAIL + " TEXT, " +
                COLUMN_REQUEST_TIME + " INTEGER, " +
                COLUMN_REQUEST_STATUS + " TEXT)";
        db.execSQL(createPasswordResetRequestTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    // Thêm user mới
    public boolean addUser(String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_EMAIL, email);
        contentValues.put(COLUMN_PASSWORD, password);
        contentValues.put(COLUMN_ROLE, role);

        long result = db.insert(TABLE_USERS, null, contentValues);
        db.close();
        return result != -1;
    }

    // Kiểm tra email đã tồn tại
    public boolean checkUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID}, COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    // Kiểm tra thông tin đăng nhập
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID}, COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?", new String[]{email, password}, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    // Kiểm tra email của user có tồn tại không
    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID}, COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Lấy vai trò của người dùng
    public String getUserRole(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String role = null;
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ROLE}, COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
        if (cursor.moveToFirst()) {
            role = cursor.getString(cursor.getColumnIndex(COLUMN_ROLE));
        }
        cursor.close();
        db.close();
        return role;
    }

    // Thêm user trong quản lý user
    public boolean addManageUser(String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_EMAIL, email);
        contentValues.put(COLUMN_PASSWORD, password);
        contentValues.put(COLUMN_ROLE, role);

        long result = db.insert(TABLE_USERS, null, contentValues);
        db.close();
        return result != -1;
    }

    // Sửa thông tin user trong quản lý user
    public boolean editUser(int userId, String newPassword, String newRole) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if (!newPassword.isEmpty()) {
            contentValues.put(COLUMN_PASSWORD, newPassword);
        }
        if (!newRole.isEmpty()) {
            contentValues.put(COLUMN_ROLE, newRole);
        }

        int result = db.update(TABLE_USERS, contentValues, COLUMN_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
        return result > 0;
    }
    // Lấy ID của user bằng email
    public int getUserIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID}, COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            cursor.close();
            return userId;
        }
        if (cursor != null) {
            cursor.close();
        }
        return -1;
    }

    // Thêm yêu cầu đặt lại mật khẩu
    public boolean addPasswordResetRequest(String email, long requestTime, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_REQUEST_EMAIL, email);
        values.put(COLUMN_REQUEST_TIME, requestTime);
        values.put(COLUMN_REQUEST_STATUS, status);

        long result = db.insert(TABLE_PASSWORD_RESET_REQUESTS, null, values);
        return result != -1; // Trả về true nếu thêm thành công, false nếu không thành công
    }

    // Xóa yêu cầu đặt lại mật khẩu theo email sau khi admin đã đổi pass
    public boolean deletePasswordResetRequest(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_PASSWORD_RESET_REQUESTS, COLUMN_REQUEST_EMAIL + "=?", new String[]{email});
        db.close();
        return rowsAffected > 0; // Trả về true nếu có ít nhất một hàng bị xóa
    }

    // Lấy danh sách nhà hàng
    public Cursor getAllRestaurants() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_RESTAURANTS, null, null, null, null, null, null);
    }
}
