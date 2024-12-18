package com.example.taskreminderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskreminderapp.adapters.TaskAdapter;
import com.example.taskreminderapp.database.DatabaseHelper;
import com.example.taskreminderapp.models.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView taskRecyclerView;
    private FloatingActionButton addTaskFab;
    private DatabaseHelper dbHelper;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // تحقق مما إذا كان المستخدم قد سجل الدخول
        if (!isLoggedIn()) {
            navigateToLoginActivity();
            return;
        }

        setContentView(R.layout.activity_main);

        taskRecyclerView = findViewById(R.id.taskRecyclerView);
        addTaskFab = findViewById(R.id.addTaskFab);
        dbHelper = new DatabaseHelper(this);

        // إعداد RecyclerView
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // تحميل المهام عند البداية
        loadTasks();

        // BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        // إضافة مهمة جديدة
        addTaskFab.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddTaskActivity.class)));
    }

    private boolean isLoggedIn() {
        return getSharedPreferences("appPrefs", MODE_PRIVATE).getBoolean("isLoggedIn", false);
    }

    private void loadTasks() {
        long userId = getUserId();
        List<Task> tasks = dbHelper.getAllTasks(userId);  // Ensure that this method returns a list of Task objects

        // Check if the list is not empty
        if (tasks != null && !tasks.isEmpty()) {
            taskAdapter = new TaskAdapter(this, tasks,
                    task -> {
                        Intent intent = new Intent(MainActivity.this, TaskDetailsActivity.class);
                        intent.putExtra("taskId", task.getId());
                        startActivity(intent);
                    },
                    task -> {
                        // إضافة تنفيذ للنقر الطويل هنا
                    }
            );



            taskRecyclerView.setAdapter(taskAdapter);
        }
    }


    private long getUserId() {
        return getSharedPreferences("appPrefs", MODE_PRIVATE).getLong("userId", -1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks(); // إعادة تحميل المهام عند العودة إلى النشاط
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_home) {
            // البقاء في الصفحة الرئيسية
            return true;
        } else if (item.getItemId() == R.id.nav_profile) {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        } else {
            return false; // إذا لم يتم التعرف على العنصر
        }
    }


    private void navigateToLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

