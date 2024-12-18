package com.example.taskreminderapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskreminderapp.R;
import com.example.taskreminderapp.TaskDiffCallback;
import com.example.taskreminderapp.models.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final Context context;
    private final List<Task> tasks;
    private final OnTaskClickListener onTaskClickListener;
    private final OnTaskLongClickListener onTaskLongClickListener;

    public TaskAdapter(Context context, List<Task> tasks,
                       OnTaskClickListener onTaskClickListener,
                       OnTaskLongClickListener onTaskLongClickListener) {
        this.context = context;
        this.tasks = tasks != null ? tasks : new ArrayList<>();
        this.onTaskClickListener = onTaskClickListener;
        this.onTaskLongClickListener = onTaskLongClickListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView descriptionTextView;
        private final TextView dueDateTextView;
        private final ImageView pinImageView;
        private final ImageView taskArrow;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.taskTitle);
            descriptionTextView = itemView.findViewById(R.id.taskDescription);
            dueDateTextView = itemView.findViewById(R.id.taskDeadline);
            pinImageView = itemView.findViewById(R.id.pinImage);
            taskArrow = itemView.findViewById(R.id.taskArrow);

            // انقر على العنصر
            itemView.setOnClickListener(v -> {
                if (onTaskClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onTaskClickListener.onTaskClick(tasks.get(position));
                    }
                }
            });

            // الضغط المطول على العنصر
            itemView.setOnLongClickListener(v -> {
                if (onTaskLongClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onTaskLongClickListener.onTaskLongClick(tasks.get(position));
                    }
                }
                return true;
            });

            // حدث عند الضغط على السهم
            taskArrow.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Task task = tasks.get(position);
                    Intent intent = new Intent(context, com.example.taskreminderapp.TaskViewActivity.class);
                    intent.putExtra("TASK_TITLE", task.getTitle());
                    intent.putExtra("TASK_DESCRIPTION", task.getDescription());
                    intent.putExtra("TASK_DEADLINE", task.getDueDate());
                    context.startActivity(intent);
                }
            });
        }

        public void bind(Task task) {
            // تعديل العنوان ليعرض أول 5 أحرف فقط
            String title = task.getTitle() != null ? task.getTitle() : "No Title";
            if (title.length() > 4) {
                title = title.substring(0, 4) + "..";
            }
            titleTextView.setText(title);

            // تعديل الوصف ليعرض أول 7 أحرف فقط
            String description = task.getDescription() != null ? task.getDescription() : "No Description";
            if (description.length() > 7) {
                description = description.substring(0, 7) + "..";
            }
            descriptionTextView.setText(description);

            // معالجة التاريخ
            String dueDate = task.getDueDate();
            Log.d("TaskAdapter", "Received Date: " + dueDate); // إضافة سجل لعرض التاريخ المستلم
            if (dueDate != null && !dueDate.isEmpty()) {
                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dueDate);
                    if (date != null) {
                        String formattedDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date);
                        dueDateTextView.setText(formattedDate);
                    } else {
                        dueDateTextView.setText("Invalid Date");
                    }
                } catch (ParseException e) {
                    dueDateTextView.setText("Invalid Date");
                    Log.e("TaskAdapter", "Date Parse Error", e);
                }
            } else {
                dueDateTextView.setText("No Due Date");
            }

            // إضافة سجل لتصحيح الحالة
            Log.d("TaskAdapter", "Task Pinned: " + task.isPinned()); // التحقق من قيمة isPinned

            // تحديث ظهور صورة الدبوس
            pinImageView.setVisibility(task.isPinned() ? View.VISIBLE : View.GONE);
        }



    }

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    public interface OnTaskLongClickListener {
        void onTaskLongClick(Task task);
    }

    public void updateTasks(@NonNull List<Task> newTasks) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new TaskDiffCallback(this.tasks, newTasks));
        this.tasks.clear();
        this.tasks.addAll(newTasks);
        diffResult.dispatchUpdatesTo(this);
    }
}
