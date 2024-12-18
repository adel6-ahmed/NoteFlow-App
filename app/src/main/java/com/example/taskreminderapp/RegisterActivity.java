package com.example.taskreminderapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taskreminderapp.database.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etEmail, etPhone, etFullName;
    private Button btnRegister;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // تهيئة العناصر
        db = new DatabaseHelper(this);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etFullName = findViewById(R.id.etFullName);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String fullName = etFullName.getText().toString().trim();

            // تحقق من أن الحقول غير فارغة
            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty() || fullName.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "يرجى ملء جميع الحقول", Toast.LENGTH_SHORT).show();
            } else {
                // إضافة المستخدم إلى قاعدة البيانات
                boolean result = db.registerUser(username, password, email, phone, fullName);
                if (result) {
                    Toast.makeText(RegisterActivity.this, "تم التسجيل بنجاح", Toast.LENGTH_SHORT).show();
                    // الانتقال إلى شاشة تسجيل الدخول
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();  // إغلاق شاشة التسجيل
                } else {
                    Toast.makeText(RegisterActivity.this, "حدث خطأ أثناء التسجيل", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
