package com.example.taskreminderapp;

public class User {
    private int id;  // تغيير id إلى long
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String password;

    // مُنشئ User مع تعيين قيم للـ username, fullName, email, phone و password
    public User(String username, String fullName, String email) {
        this.id = id;  // تعيين id عند إنشاء الكائن
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    // Getters
    public int getId() {
        return (int) id;  // إرجاع id كـ long
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
