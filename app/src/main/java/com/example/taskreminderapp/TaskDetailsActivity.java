package com.example.taskreminderapp;

import android.os.Bundle;
import android.util.Log; // Log kütüphanesini içe aktar
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taskreminderapp.database.DatabaseHelper;
import com.example.taskreminderapp.models.Task;

public class TaskDetailsActivity extends AppCompatActivity {
    private static final String TAG = "TaskDetailsActivity"; // Loglar için TAG tanımı
    private EditText baslikEditText, aciklamaEditText, sonTarihEditText;
    private Button guncelleGorevButonu, silGorevButonu;
    private DatabaseHelper dbHelper;
    private int gorevId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        baslikEditText = findViewById(R.id.titleEditText);
        aciklamaEditText = findViewById(R.id.descriptionEditText);
        sonTarihEditText = findViewById(R.id.deadlineEditText);
        guncelleGorevButonu = findViewById(R.id.updateTaskButton);
        silGorevButonu = findViewById(R.id.deleteTaskButton);
        dbHelper = new DatabaseHelper(this);

        long gorevId = getIntent().getLongExtra("taskId", -1L); // Burada Long yerine long kullanıyoruz
        Log.d("TaskDetailsActivity", "Intent'ten alınan görevId: " + gorevId);

        // getTaskById kullanarak veriyi getir
        Task gorev = dbHelper.getTaskById(gorevId);

        // Görevin varlığını kontrol et
        if (gorev != null) {
            Log.d(TAG, "Görev bulundu: " + gorev.toString()); // Görev detaylarını logla
            baslikEditText.setText(gorev.getTitle());
            aciklamaEditText.setText(gorev.getDescription());

            // Tarih formatının doğru olduğundan emin ol
            String sonTarih = gorev.getDueDate(); // getDueDate'in uygun format döndürdüğünden emin olun
            if (sonTarih != null) {
                sonTarihEditText.setText(sonTarih);
            } else {
                sonTarihEditText.setText(""); // Tarih değeri boşsa
            }
        } else {
            Log.w(TAG, "Bu görevId ile görev bulunamadı: " + gorevId); // Görev bulunamazsa uyarı logla
            Toast.makeText(this, "Görev bulunamadı!", Toast.LENGTH_SHORT).show();
            finish(); // Görev bulunamazsa aktiviteyi kapat
        }

        // Güncelle butonunun işlemi
        guncelleGorevButonu.setOnClickListener(v -> {
            String baslik = baslikEditText.getText().toString();
            String aciklama = aciklamaEditText.getText().toString();
            String sonTarih = sonTarihEditText.getText().toString();

            Log.d(TAG, "Güncelle butonuna tıklandı. Başlık: " + baslik + ", Son Tarih: " + sonTarih); // Giriş verilerini logla

            if (!baslik.isEmpty() && !sonTarih.isEmpty()) {
                // GörevId ile güncellenmiş bir görev oluştur
                Task guncellenmisGorev = new Task(gorevId, baslik, aciklama, "Varsayılan Kategori", sonTarih);
                boolean guncellemeBasarili = dbHelper.updateTask(guncellenmisGorev); // Görevi güncelle
                if (guncellemeBasarili) {
                    Log.i(TAG, "Görev başarıyla güncellendi: " + guncellenmisGorev.toString()); // Güncelleme başarılıysa logla
                    Toast.makeText(this, "Görev güncellenemedi!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Görev güncellenemedi."); // Güncelleme başarısızsa hata logla
                    Toast.makeText(this, "Görev başarıyla güncellendi!", Toast.LENGTH_SHORT).show();
                }
                finish(); // Aktiviteyi kapat
            } else {
                Log.w(TAG, "Zorunlu alanlar boş."); // Zorunlu alanlar boşsa uyarı logla
                Toast.makeText(this, "Lütfen tüm zorunlu alanları doldurun!", Toast.LENGTH_SHORT).show();
            }
        });

        // Sil butonunun işlemi
        silGorevButonu.setOnClickListener(v -> {
            Log.d(TAG, "Sil butonuna tıklandı, görevId: " + gorevId); // Silme işlemi logla
            boolean silmeBasarili = dbHelper.deleteTask(gorevId); // Görevi görevId ile sil
            if (silmeBasarili) {
                Log.i(TAG, "Görev başarıyla silindi. GörevId: " + gorevId); // Silme başarılıysa logla
                Toast.makeText(this, "Görev silindi!", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Görev silinemedi. GörevId: " + gorevId); // Silme başarısızsa hata logla
                Toast.makeText(this, "Görev silinemedi!", Toast.LENGTH_SHORT).show();
            }
            finish(); // Aktiviteyi kapat
        });
    }
}
