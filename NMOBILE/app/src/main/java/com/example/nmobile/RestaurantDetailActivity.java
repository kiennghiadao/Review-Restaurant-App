package com.example.nmobile;

import android.content.ContentValues; // Nhập khẩu lớp ContentValues để lưu trữ giá trị
import android.database.Cursor; // Nhập khẩu lớp Cursor để quản lý kết quả truy vấn
import android.database.sqlite.SQLiteDatabase; // Nhập khẩu lớp SQLiteDatabase để làm việc với cơ sở dữ liệu
import android.os.Bundle; // Nhập khẩu lớp Bundle để quản lý trạng thái
import android.view.View; // Nhập khẩu lớp View để xử lý tương tác
import android.widget.Button; // Nhập khẩu lớp Button để tạo nút
import android.widget.EditText; // Nhập khẩu lớp EditText để nhận đầu vào văn bản
import android.widget.ImageView; // Nhập khẩu lớp ImageView để hiển thị hình ảnh
import android.widget.LinearLayout; // Nhập khẩu lớp LinearLayout để tạo bố cục
import android.widget.RatingBar; // Nhập khẩu lớp RatingBar để hiển thị đánh giá
import android.widget.TextView; // Nhập khẩu lớp TextView để hiển thị văn bản
import android.widget.Toast; // Nhập khẩu lớp Toast để hiển thị thông báo
import androidx.appcompat.app.AppCompatActivity; // Nhập khẩu lớp AppCompatActivity để sử dụng hoạt động cơ sở

public class RestaurantDetailActivity extends AppCompatActivity {
    // Khai báo các biến để liên kết với các view trong layout
    private ImageView imageViewDetail;
    private TextView nameTextView;
    private TextView locationTextView;
    private TextView typeTextView;
    private TextView detailsTextView;
    private RatingBar ratingBarDetail;
    private Button addCommentButton;
    private LinearLayout commentsContainer;
    private LinearLayout addCommentLayout;
    private RatingBar commentRatingBar;
    private EditText commentEditText;
    private Button submitCommentButton;
    private Button cancelCommentButton;
    private int restaurantId;// ID của nhà hàng
    private UserSessionManager userSessionManager;// Quản lý phiên làm việc của người dùng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        // Khởi tạo UserSessionManager
        userSessionManager = new UserSessionManager(this);

        // Liên kết các view từ layout
        imageViewDetail = findViewById(R.id.imageViewDetail);
        nameTextView = findViewById(R.id.textViewName);
        locationTextView = findViewById(R.id.textViewLocation);
        typeTextView = findViewById(R.id.textViewType);
        detailsTextView = findViewById(R.id.textViewDetails);
        ratingBarDetail = findViewById(R.id.ratingBarDetail);
        addCommentButton = findViewById(R.id.buttonAddComment);
        commentsContainer = findViewById(R.id.commentsContainer);
        addCommentLayout = findViewById(R.id.addCommentLayout);
        commentRatingBar = findViewById(R.id.commentRatingBar);
        commentEditText = findViewById(R.id.editTextComment);
        submitCommentButton = findViewById(R.id.buttonSubmitComment);
        cancelCommentButton = findViewById(R.id.buttonCancelComment);

        restaurantId = getIntent().getIntExtra("restaurant_id", -1);

        // Nếu ID hợp lệ, tải dữ liệu nhà hàng và bình luận
        if (restaurantId != -1) {
            loadRestaurantData(restaurantId);
            loadComments(restaurantId);
        }

        // Thêm sự kiện cho nút thêm bình luận
        addCommentButton.setOnClickListener(v -> {
            addCommentLayout.setVisibility(View.VISIBLE);
        });

        // Thêm sự kiện cho nút gửi bình luận
        submitCommentButton.setOnClickListener(v -> {
            // Lấy nội dung bình luận
            String comment = commentEditText.getText().toString().trim();
            float rating = commentRatingBar.getRating();// Lấy điểm đánh giá
            if (rating > 5.0) {
                rating = 5.0f;// Giới hạn điểm đánh giá tối đa
            }
            if (!comment.isEmpty() || rating > 0) {
                saveComment(comment, rating);// Lưu bình luận
                Toast.makeText(RestaurantDetailActivity.this, "Comment added successfully!", Toast.LENGTH_SHORT).show();
                addCommentLayout.setVisibility(View.GONE);// Ẩn layout thêm bình luận
                loadComments(restaurantId); // Làm mới danh sách bình luận ngay lập tức
            } else {
                Toast.makeText(RestaurantDetailActivity.this, "Please enter a comment or a rating.", Toast.LENGTH_SHORT).show();
            }
        });

        // Thêm sự kiện cho nút hủy bình luận
        cancelCommentButton.setOnClickListener(v -> {
            addCommentLayout.setVisibility(View.GONE);
        });
    }

    private void loadRestaurantData(int restaurantId) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();// Mở cơ sở dữ liệu ở chế độ đọc

        Cursor cursor = db.query(DatabaseHelper.TABLE_RESTAURANTS, null,
                DatabaseHelper.COLUMN_RESTAURANT_ID + "=?", new String[]{String.valueOf(restaurantId)},
                null, null, null);

        if (cursor.moveToFirst()) {// Nếu có kết quả
            Restaurant restaurant = new Restaurant(// Tạo đối tượng Restaurant từ kết quả truy vấn
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESTAURANT_ID)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESTAURANT_NAME)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_LOCATION)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TYPE)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DETAILS)),
                    cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESTAURANT_RATING)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE_URL))
            );
            // Cập nhật các view với thông tin nhà hàng
            nameTextView.setText(restaurant.getName());
            locationTextView.setText(restaurant.getLocation());
            typeTextView.setText(restaurant.getType());
            detailsTextView.setText(restaurant.getDetails());
            ratingBarDetail.setRating(restaurant.getRating());
            imageViewDetail.setImageResource(restaurant.getImageResId());
        }
        cursor.close();
    }


    private void loadComments(int restaurantId) {// Phương thức tải bình luận
        commentsContainer.removeAllViews();// Xóa tất cả bình luận cũ
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();// Mở cơ sở dữ liệu ở chế độ đọc

        // Truy vấn để lấy bình luận của nhà hàng
        String query = "SELECT comments.content, users.email " +
                "FROM comments " +
                "INNER JOIN users ON comments.user_id = users.id " +
                "WHERE comments.restaurant_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(restaurantId)});

        while (cursor.moveToNext()) {// Lặp qua các kết quả
            String comment = cursor.getString(cursor.getColumnIndex("content"));
            String userName = cursor.getString(cursor.getColumnIndex("email"));

            // Tạo TextView để hiển thị bình luận
            TextView commentTextView = new TextView(this);
            commentTextView.setText(userName + ": " + comment);
            commentTextView.setPadding(8, 8, 8, 8);

            commentsContainer.addView(commentTextView);// Thêm bình luận vào container
        }
        cursor.close();
    }

    private void saveComment(String comment, float rating) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();// Mở cơ sở dữ liệu ở chế độ ghi

        int userId = userSessionManager.getUserId();// Lấy ID người dùng từ phiên làm việc
        // Tạo ContentValues để lưu trữ dữ liệu bình luận
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_REVIEW_RESTAURANT_ID, restaurantId);
        values.put(DatabaseHelper.COLUMN_CONTENT, comment);
        values.put(DatabaseHelper.COLUMN_COMMENT_USER_ID, userId);
        values.put(DatabaseHelper.COLUMN_RATING, rating);

        db.insert(DatabaseHelper.TABLE_COMMENTS, null, values);// Chèn bình luận vào cơ sở dữ liệu

        updateRestaurantRating();// Cập nhật điểm đánh giá cho nhà hàng

        db.close();
    }

    private void updateRestaurantRating() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Truy vấn để tính toán điểm đánh giá trung bình
        String avgRatingQuery = "SELECT AVG(" + DatabaseHelper.COLUMN_RATING + ") as avg_rating " +
                "FROM " + DatabaseHelper.TABLE_COMMENTS + " " +
                "WHERE " + DatabaseHelper.COLUMN_REVIEW_RESTAURANT_ID + " = ?";
        Cursor cursor = db.rawQuery(avgRatingQuery, new String[]{String.valueOf(restaurantId)});
        float avgRating = 0;
        if (cursor.moveToFirst()) {// Nếu có kết quả
            avgRating = cursor.getFloat(cursor.getColumnIndex("avg_rating"));// Lấy điểm đánh giá trung bình
        }
        cursor.close();

        if (avgRating > 5.0) {// Giới hạn điểm đánh giá tối đa
            avgRating = 5.0f;
        }

        // Cập nhật điểm đánh giá cho nhà hàng
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_RESTAURANT_RATING, avgRating);
        db.update(DatabaseHelper.TABLE_RESTAURANTS, values, DatabaseHelper.COLUMN_RESTAURANT_ID + "=?", new String[]{String.valueOf(restaurantId)});
        // Tải lại dữ liệu nhà hàng để cập nhật điểm đánh giá
        loadRestaurantData(restaurantId);

        db.close();
    }
}

