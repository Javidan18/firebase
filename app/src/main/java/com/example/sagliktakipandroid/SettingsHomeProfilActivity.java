package com.example.sagliktakipandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Logcat için import
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsHomeProfilActivity extends AppCompatActivity {

    private TextView userNameInput; // Kullanıcı adı TextView
    private TextView emailInput; // E-posta TextView
    private String userId; // Kullanıcı ID'sini saklamak için değişken

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingshomeprofil); // XML dosyasını ayarlayın

        // Kullanıcı ID'sini al
        userId = getIntent().getStringExtra("USER_ID");

        if (userId == null) {
            Toast.makeText(this, "Kullanıcı ID'si alınamadı.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Logcat'te kullanıcı ID'sini göster
        Log.d("SettingsHomeProfilActivity", "Kullanıcı ID: " + userId);

        // Geri dön butonunu bul
        ImageButton backButton = findViewById(R.id.backButtonProfil);
        userNameInput = findViewById(R.id.userNameInput); // Kullanıcı adı gösterimi için TextView
        emailInput = findViewById(R.id.emailInput); // E-posta gösterimi için TextView

        // Kullanıcı bilgilerini Firebase'den al
        loadUserData();

        // Geri dön butonuna tıklama dinleyicisi ekle
        backButton.setOnClickListener(v -> navigateToSettingsHomeActivity());
    }

    private void loadUserData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Kullanıcının e-posta ve kullanıcı adını çekmek için dinleyici ekle
        databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String email = snapshot.child("email").getValue(String.class);
                    String username = snapshot.child("username").getValue(String.class);

                    // Bilgileri TextView'lerde göster
                    userNameInput.setText(username != null ? username : "Kullanıcı adı bulunamadı.");
                    emailInput.setText(email != null ? email : "E-posta bulunamadı.");
                } else {
                    Toast.makeText(SettingsHomeProfilActivity.this, "Kullanıcı bilgileri bulunamadı.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SettingsHomeProfilActivity.this, "Veri yüklenirken hata oluştu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToSettingsHomeActivity() {
        Intent intent = new Intent(SettingsHomeProfilActivity.this, SettingsHomeActivity.class);
        intent.putExtra("USER_ID", userId); // USER_ID'yi geri gönder
        startActivity(intent);
        finish(); // Mevcut aktiviteyi kapat
    }
}
