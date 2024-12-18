package com.example.taskreminderapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskreminderapp.adapters.TaskAdapter;
import com.example.taskreminderapp.database.DatabaseHelper;
import com.example.taskreminderapp.models.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private RecyclerView taskRecyclerView;
    private FloatingActionButton addTaskFab;
    private EditText searchEditText;
    private DatabaseHelper dbHelper;
    private TaskAdapter taskAdapter;
    private List<Task> allTasks = new ArrayList<>();
    private int userId;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home;
    }

    @Override
    protected void setSelectedNavItem(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Retrieve userId from SharedPreferences
        userId = (int) getUserId();
        if (userId == -1) {
            navigateToLogin();
            return;
        }

        // Initialize UI components
        initViews();
        initRecyclerView();
        setupSearch();
        setupBottomNavigation();
        setupAddTaskButton();
        loadTasks();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    private long getUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences("appPrefs", MODE_PRIVATE);
        return sharedPreferences.getLong("userId", -1);
    }

    private void navigateToLogin() {
        Toast.makeText(this, "User ID not found. Please log in.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void initViews() {
        taskRecyclerView = findViewById(R.id.taskRecyclerView);
        addTaskFab = findViewById(R.id.addTaskFab);
        searchEditText = findViewById(R.id.searchEditText);
        dbHelper = new DatabaseHelper(this);
    }

    private void initRecyclerView() {
        int numberOfColumns = 3; // Number of columns in the grid
        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);
        taskRecyclerView.setLayoutManager(layoutManager);

        taskAdapter = new TaskAdapter(this, allTasks, new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onTaskClick(Task task) {
                if (task != null) {
                    Log.d("HomeActivity", "Task object received: " + task.toString()); // تسجيل تفاصيل المهمة

                    long taskId = task.getId();
                    Log.d("HomeActivity", "Task ID clicked: " + taskId);

                    if (taskId > 0) {
                        Log.i("HomeActivity", "Valid Task ID: " + taskId + ". Navigating to TaskDetailsActivity.");
                        Intent intent = new Intent(HomeActivity.this, TaskDetailsActivity.class);
                        intent.putExtra("taskId", taskId);
                        startActivity(intent);
                    } else {
                        Log.e("HomeActivity", "Invalid Task ID: " + taskId);
                        Toast.makeText(HomeActivity.this, "Error: Invalid Task ID!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("HomeActivity", "Task object is null!");
                    Toast.makeText(HomeActivity.this, "Error: Task is null!", Toast.LENGTH_SHORT).show();
                }
            }





        }, new TaskAdapter.OnTaskLongClickListener() {
            @Override
            public void onTaskLongClick(Task task) {
                // Action on long task press
                Toast.makeText(HomeActivity.this, "Long pressed: " + task.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        // Adding item decoration for spacing
        taskRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.set(8, 8, 8, 8); // Spacing of 8dp on all sides
            }
        });

        taskRecyclerView.setAdapter(taskAdapter);
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTasks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterTasks(String query) {
        List<Task> filteredTasks = dbHelper.searchTasks(userId, query);
        if (filteredTasks != null && !filteredTasks.isEmpty()) {
            taskAdapter.updateTasks(filteredTasks);
        } else {
            Toast.makeText(this, "No matching tasks found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupAddTaskButton() {
        addTaskFab.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, AddTaskActivity.class)));
    }

    private void loadTasks() {
        try {
            List<Task> tasks = dbHelper.getAllTasks(userId);
            if (tasks == null || tasks.isEmpty()) {
                Toast.makeText(this, "No tasks available.", Toast.LENGTH_SHORT).show();
            } else {
                updateTaskList(tasks);
            }
        } catch (Exception e) {
            Log.e("HomeActivity", "Error loading tasks: ", e);
            Toast.makeText(this, "An error occurred while loading tasks.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTaskList(List<Task> newTasks) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new TaskDiffCallback(allTasks, newTasks));
        allTasks.clear();
        allTasks.addAll(newTasks);
        diffResult.dispatchUpdatesTo(taskAdapter);
    }

    private void deleteTask(Task task) {
        boolean isDeleted = dbHelper.deleteTask(task.getId());
        if (isDeleted) {
            allTasks.remove(task);
            taskAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Task deleted successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to delete task.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            return true;
        } else if (itemId == R.id.nav_profile) {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.nav_settings) {
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            return true;
        } else {
            return false;
        }
    }
}