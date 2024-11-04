package com.example.sagliktakipandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Logcat için import
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsHomeActivity extends AppCompatActivity {

    private String userId; // Kullanıcı ID'sini saklamak için değişken
    private DatabaseReference databaseReference; // Realtime Database referansı

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingshome); // XML dosyasını ayarlayın

        // Kullanıcı ID'sini alın
        userId = getIntent().getStringExtra("USER_ID"); // USER_ID'yi alın

        // Kullanıcı ID'sini Logcat'te göster
        Log.d("SettingsHomeActivity", "Kullanıcı ID: " + userId);

        // Firebase Database referansını ayarlayın
        databaseReference = FirebaseDatabase.getInstance().getReference("Users"); // Düzeltildi

        // Geri dön butonunu bul
        ImageButton backButton = findViewById(R.id.backButton);

        // Logout butonunu bul
        Button logoutButton = findViewById(R.id.logoutButton);

        // Profil butonunu bul
        Button profileButton = findViewById(R.id.profileButton);

        // Geri dön butonuna tıklama dinleyicisi ekle
        backButton.setOnClickListener(v -> navigateToHomeActivity());

        // Logout butonuna tıklama dinleyicisi ekle
        logoutButton.setOnClickListener(v -> navigateToLoginActivity());

        // Profil butonuna tıklama dinleyicisi ekle
        profileButton.setOnClickListener(v -> loadUserData());
    }

    private void navigateToHomeActivity() {
        Intent intent = new Intent(SettingsHomeActivity.this, HomeActivity.class);
        startActivity(intent);
        finish(); // Mevcut aktiviteyi kapat
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(SettingsHomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Mevcut aktiviteyi kapat
    }

    private void loadUserData() {
        // Kullanıcı bilgilerini Firebase'den al
        databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Kullanıcı bilgilerini al
                    String email = snapshot.child("email").getValue(String.class);
                    String username = snapshot.child("username").getValue(String.class);

                    // Kullanıcı bilgilerini Profil Aktivitesine gönder
                    Intent intent = new Intent(SettingsHomeActivity.this, SettingsHomeProfilActivity.class);
                    intent.putExtra("USER_ID", userId); // USER_ID'yi gönder
                    intent.putExtra("USER_EMAIL", email);
                    intent.putExtra("USER_USERNAME", username);
                    startActivity(intent); // Profil aktivitesine geçiş yap
                    finish(); // Mevcut aktiviteyi kapat
                } else {
                    Toast.makeText(SettingsHomeActivity.this, "Kullanıcı bilgileri bulunamadı.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Hata mesajı
                Toast.makeText(SettingsHomeActivity.this, "Veri yüklenirken hata oluştu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
