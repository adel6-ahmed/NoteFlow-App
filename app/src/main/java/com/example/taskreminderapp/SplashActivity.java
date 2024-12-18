package com.example.taskreminderapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // Bu, giriş ekranının XML dosyası olduğundan emin olun

        // Handler kullanarak 5 saniye gecikme ile geçiş yap
        new Handler().postDelayed(() -> {
            // Giriş durumunu kontrol et
            if (girisYapildiMi()) {
                // Kullanıcı daha önce giriş yaptıysa HomeActivity'ye git
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            } else {
                // Giriş yapılmadıysa LoginActivity'ye git
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish(); // SplashActivity'yi bitir, böylece geri dönülemez
        }, 5000); // 5000 milisaniye (5 saniye) gecikme
    }

    private boolean girisYapildiMi() {
        // SharedPreferences'tan giriş durumunu kontrol et
        SharedPreferences sharedPreferences = getSharedPreferences("appPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }
}
