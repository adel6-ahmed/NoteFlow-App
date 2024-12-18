package com.example.taskreminderapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taskreminderapp.database.DatabaseHelper;
import com.example.taskreminderapp.models.Task;

public class AddTaskActivity extends AppCompatActivity {

    private EditText titleEditText, descriptionEditText, deadlineEditText;
    private Button saveTaskButton;
    private DatabaseHelper dbHelper;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Initialize UI elements
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        deadlineEditText = findViewById(R.id.deadlineEditText);
        saveTaskButton = findViewById(R.id.saveTaskButton);
        dbHelper = new DatabaseHelper(this);

        // Retrieve userId from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("appPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getLong("userId", -1L);  // استخدم getLong لاسترجاع قيمة userId

        if (userId == -1L) {
            // If userId is not found
            Toast.makeText(this, "Unable to find user ID.", Toast.LENGTH_SHORT).show();
            finish();  // End activity if no userId exists
            return;
        }

        // Save task when button is clicked
        saveTaskButton.setOnClickListener(v -> {
            // Get task details
            String title = titleEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String deadline = deadlineEditText.getText().toString();

            // If deadline is empty, set it to "No Due Date"
            if (deadline.isEmpty()) {
                deadline = "No Due Date";
            }

            // Validate fields
            if (!title.isEmpty()) {
                // Create a new task with default category ("General")
                long taskId = 0;
                Task task = new Task(taskId, title, description, "General", deadline);  // لا حاجة لـ id هنا

                // Set the userId to associate the task with the user
                task.setUserId(userId);

                // Insert task into the database
                taskId = dbHelper.insertTask(task);

                if (taskId != -1L) {
                    // Optionally update task ID in the object after insertion
                    task.setId(taskId);
                    Toast.makeText(this, "Task saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error saving task.", Toast.LENGTH_SHORT).show();
                }

                // Return to previous activity
                finish();
            } else {
                // Show error message if fields are empty
                Toast.makeText(this, "Please fill in all required fields!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
