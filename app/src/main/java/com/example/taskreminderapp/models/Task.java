package com.example.taskreminderapp.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Task {
    private long id;
    private String title;
    private String description;
    private String category;
    private String dueDate;
    private long userId;
    private boolean isPinned;

    // Constructor with ID, title, and description only


    // Constructor with taskId, title, description, category, dueDate, and userId
    public Task(long taskId, String title, String description, String category, String dueDate) {
        this.id = taskId; // Correctly setting taskId
        this.title = title;
        this.description = description;
        this.category = category != null ? category : ""; // Use empty string if category is null
        this.dueDate = dueDate != null ? dueDate : ""; // Use empty string if dueDate is null
        this.userId = userId;
        this.isPinned = true; // Default value
    }

    // Constructor with all fields
    public Task(long id, String title, String description, String category, String dueDate, long userId, boolean isPinned) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.dueDate = dueDate != null ? dueDate : ""; // Default to empty string if null
        this.userId = userId;
        this.isPinned = isPinned;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    // Override equals method
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return id == task.id;
    }

    // Override hashCode method
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Override toString method
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", dueDate='" + dueDate + '\'' +
                ", userId=" + userId +
                ", isPinned=" + isPinned +
                '}';
    }

    // Method to format the due date
    public String getFormattedDueDate() {
        System.out.println("dueDate value: " + dueDate);

        if (dueDate == null || dueDate.isEmpty()) {
            return "1996/09/28"; // Default date
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dueDate);
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "1996/09/28"; // Default date in case of error
        }
    }
}
