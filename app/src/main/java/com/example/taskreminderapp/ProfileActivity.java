package com.example.taskreminderapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskreminderapp.database.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends BaseActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_profile;
    }

    @Override
    protected void setSelectedNavItem(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Retrieve the userId from SharedPreferences or Intent
        long userId = getUserIdFromIntentOrSharedPreferences();
        Log.d("ProfileActivity", "Received userId: " + userId);

        // Validate the userId
        if (userId == -1) {
            showToastAndRedirectToLogin("Kullanıcı kimliği bulunamadı. Lütfen giriş yapınız.");
            return;
        }

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);

        // Fetch and display user data
        if (!populateUserData(userId)) {
            showToastAndRedirectToLogin("Kullanıcı verileri bulunamadı.");
            return;
        }

        // Display the count of tasks for the user
        displayTaskCount(userId);

        // Set up the BottomNavigationView
        setupBottomNavigationView();
    }

    private long getUserIdFromIntentOrSharedPreferences() {
        long userId = getIntent().getLongExtra("userId", -1);

        if (userId == -1) {
            SharedPreferences sharedPreferences = getSharedPreferences("appPrefs", MODE_PRIVATE);
            userId = sharedPreferences.getLong("userId", -1);
        }

        return userId;
    }

    private void showToastAndRedirectToLogin(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean populateUserData(long userId) {
        User user = databaseHelper.getUserById(userId);
        if (user != null) {
            TextView userNameTextView = findViewById(R.id.userName);
            TextView userEmailTextView = findViewById(R.id.userEmail);
            TextView userPhoneTextView = findViewById(R.id.userPhone);

            userNameTextView.setText(user.getFullName());
            userEmailTextView.setText(user.getEmail());
            userPhoneTextView.setText(user.getPhone());
            return true;
        }
        return false;
    }

    private void displayTaskCount(long userId) {
        int taskCount = databaseHelper.getTaskCountForUser(userId);
        TextView noteCountValueTextView = findViewById(R.id.noteCountValue);
        noteCountValueTextView.setText(String.valueOf(taskCount));
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        setSelectedNavItem(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_home) {
            startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            finish();
            return true;
        } else if (item.getItemId() == R.id.nav_profile) {
            // User is already on the profile page
            return true;
        } else if (item.getItemId() == R.id.nav_settings) {
            startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
            finish();
            return true;
        } else {
            return false;
        }
    }
}