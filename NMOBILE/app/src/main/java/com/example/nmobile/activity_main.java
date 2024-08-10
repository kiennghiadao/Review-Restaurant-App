package com.example.nmobile;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class activity_main extends AppCompatActivity {

    private ImageButton profileButton;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Truy xuất vai trò người dùng từ Intent
        Intent intent = getIntent();
        userRole = intent.getStringExtra("User Role");


        //Mở nút profile
        profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowProfileOptions();
            }
        });
    }

    private void ShowProfileOptions()
    {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_profile_options);

        // Set up common menu items
        Button viewReviewHistory = dialog.findViewById(R.id.view_review_history);
        Button viewVisitedRestaurants = dialog.findViewById(R.id.view_visited_restaurants);
        Button logout = dialog.findViewById(R.id.logout);

        viewReviewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        viewVisitedRestaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_main.this, activity_login.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });

        if("admin".equals(userRole))
        {
            Button manageUsers = dialog.findViewById(R.id.manage_users);
            Button manageCategories = dialog.findViewById(R.id.manage_categories);
            Button manageRestaurants = dialog.findViewById(R.id.manage_restaurants);
            Button manageReviews = dialog.findViewById(R.id.manage_reviews);

            manageUsers.setVisibility(View.VISIBLE);
            manageCategories.setVisibility(View.VISIBLE);
            manageRestaurants.setVisibility(View.VISIBLE);
            manageReviews.setVisibility(View.VISIBLE);

            manageUsers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity_main.this, Manage_Users.class);
                    startActivity(intent);
                }
            });

            manageCategories.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle manage categories
                }
            });

            manageRestaurants.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle manage restaurants
                }
            });

            manageReviews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle manage reviews
                }
            });
        }
        dialog.show();
    }
};
