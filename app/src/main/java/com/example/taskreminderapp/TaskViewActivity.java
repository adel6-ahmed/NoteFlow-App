package com.example.taskreminderapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TaskViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);

        // ربط عناصر الواجهة
        TextView taskTitle = findViewById(R.id.taskTitle);
        TextView taskDescription = findViewById(R.id.taskDescription);
        TextView taskDeadline = findViewById(R.id.taskDeadline);

        // استقبال البيانات من النشاط السابق
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("TASK_TITLE");
            String description = intent.getStringExtra("TASK_DESCRIPTION");
            String deadline = intent.getStringExtra("TASK_DEADLINE");

            // عرض البيانات في النصوص
            taskTitle.setText(title != null ? title : getString(R.string.no_title));
            taskDescription.setText(description != null ? description : getString(R.string.no_description));
            taskDeadline.setText(deadline != null ? deadline : getString(R.string.no_due_date));
        }
    }
}
