package com.example.taskreminderapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taskreminderapp.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText etLoginUsername, etLoginPassword; // Kullanıcı adı ve şifre giriş alanları
    private Button btnLogin; // Giriş butonu
    private TextView tvRegisterLink; // Kayıt olma bağlantısı
    private DatabaseHelper db; // Veritabanı yardımcı sınıfı

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Arayüz bileşenlerini başlat
        db = new DatabaseHelper(this);
        etLoginUsername = findViewById(R.id.etLoginUsername);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);

        // Oturum durumunu kontrol et, giriş ekranını atla
        SharedPreferences sharedPreferences = getSharedPreferences("appPrefs", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            navigateToHomeActivity(); // Kullanıcı zaten giriş yaptıysa anasayfaya geç
        }

        // Giriş butonuna tıklama olayını işle
        btnLogin.setOnClickListener(v -> {
            String username = etLoginUsername.getText().toString().trim();
            String password = etLoginPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                // Boş alanları kontrol et
                Toast.makeText(LoginActivity.this, "Lütfen tüm alanları doldurunuz", Toast.LENGTH_SHORT).show();
            } else {
                // Kullanıcı bilgilerini kontrol et
                long userId = db.checkLoginCredentials(username, password);
                if (userId != -1) {
                    // Kimlik doğrulama başarılıysa oturum durumunu kaydet
                    saveLoginState(sharedPreferences, username, userId);
                    navigateToHomeActivity(); // Anasayfaya yönlendir
                } else {
                    // Giriş başarısız oldu
                    Toast.makeText(LoginActivity.this, "Hatalı bilgiler", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Kayıt bağlantısına tıklama olayını işle
        tvRegisterLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    /**
     * Oturum durumunu SharedPreferences'e kaydet.
     */
    private void saveLoginState(SharedPreferences sharedPreferences, String username, long userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true); // Oturum durumunu ayarla
        editor.putLong("userId", userId); // Kullanıcı kimliğini kaydet
        editor.putString("username", username); // Kullanıcı adını kaydet
        editor.apply();

        Toast.makeText(LoginActivity.this, "Başarıyla giriş yaptınız", Toast.LENGTH_SHORT).show();
    }

    /**
     * Anasayfa aktivitesine geçiş yap.
     */
    private void navigateToHomeActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish(); // Bu aktiviteyi kapat, geri dönüşü engelle
    }
}
