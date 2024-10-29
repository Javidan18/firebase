package com.example.sagliktakipandroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, passwordEditText, repeatPasswordEditText;
    private DatabaseReference databaseReference; // Realtime Database referansı
    private ProgressDialog progressDialog; // Yüklenme göstergesi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Database referansını al
        databaseReference = FirebaseDatabase.getInstance().getReference("Users"); // "Users" adında bir referans oluştur

        // UI elemanlarını tanımla
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText);

        // Yüklenme göstergesi ayarları
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Kayıt işlemi devam ediyor...");
        progressDialog.setCancelable(false);

        // Kayıt olma butonu tıklama olayı
        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this::onRegisterButtonClick);

        // LoginActivity'ye geri dönme butonu tıklama olayı
        Button backToLoginButton = findViewById(R.id.backToLoginButton);
        backToLoginButton.setOnClickListener(this::onBackToLoginButtonClick);
    }

    // Kayıt olma butonuna tıklanıldığında çağrılacak fonksiyon
    public void onRegisterButtonClick(View view) {
        createUser(view);
    }

    // Geri LoginActivity'ye dönme butonu tıklanıldığında çağrılacak fonksiyon
    public void onBackToLoginButtonClick(View view) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Kullanıcı kaydetme fonksiyonu
    private void createUser(View view) {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String repeatPassword = repeatPasswordEditText.getText().toString().trim();

        // Kullanıcı adı, email ve şifre kontrolleri
        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Kullanıcı adı gerekli");
            Log.e("RegisterActivity", "Kullanıcı adı boş.");
            return;
        }

        if (TextUtils.isEmpty(email) || !email.endsWith("@gmail.com")) {
            emailEditText.setError("Geçerli bir Gmail adresi girin");
            Log.e("RegisterActivity", "Geçersiz email: " + email);
            return;
        }

        // Şifre ve tekrar şifre kontrolleri
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Şifre gerekli");
            Log.e("RegisterActivity", "Şifre boş.");
            return;
        }

        if (TextUtils.isEmpty(repeatPassword)) {
            repeatPasswordEditText.setError("Şifreyi tekrar girin");
            Log.e("RegisterActivity", "Şifre tekrar boş.");
            return;
        }

        if (!password.equals(repeatPassword)) {
            repeatPasswordEditText.setError("Şifreler eşleşmiyor");
            Log.e("RegisterActivity", "Şifreler eşleşmiyor: " + password + " != " + repeatPassword);
            return;
        }

        // Yüklenme göstergesini göster
        progressDialog.show();

        // Mevcut kullanıcıları kontrol et
        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Aynı e-posta zaten mevcut
                    progressDialog.dismiss();
                    emailEditText.setError("Farklı bir e-posta giriniz");
                    Log.e("RegisterActivity", "E-posta zaten mevcut: " + email);
                } else {
                    // Kullanıcı bilgilerini Realtime Database'e kaydet
                    String userId = databaseReference.push().getKey(); // Yeni bir kullanıcı ID'si oluştur
                    User newUser = new User(username, email, password); // Kullanıcı bilgilerini içeren bir User nesnesi oluştur
                    if (userId != null) {
                        databaseReference.child(userId).setValue(newUser) // Kullanıcı bilgilerini veritabanına ekle
                                .addOnCompleteListener(databaseTask -> {
                                    // Yüklenme göstergesini gizle
                                    progressDialog.dismiss();
                                    if (databaseTask.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Kayıt Başarılı", Toast.LENGTH_SHORT).show();
                                        // LoginActivity'ye geçiş yap
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Log.e("RegisterActivity", "Database error: " + databaseTask.getException());
                                        Snackbar.make(view, "Kullanıcı bilgileri kaydedilemedi.", Snackbar.LENGTH_LONG).show();
                                    }
                                });
                    } else {
                        progressDialog.dismiss();
                        Log.e("RegisterActivity", "Kullanıcı ID'si oluşturulamadı.");
                        Snackbar.make(view, "Kullanıcı kaydı sırasında hata oluştu.", Snackbar.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Log.e("RegisterActivity", "Database error: " + databaseError.getMessage());
                Snackbar.make(view, "Veritabanı hatası: " + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    // User sınıfı
    public static class User {
        public String username;
        public String email;
        public String password; // Şifre alanı eklendi

        public User() {
            // Boş constructor gerekli
        }

        public User(String username, String email, String password) {
            this.username = username;
            this.email = email;
            this.password = password; // Şifre alanı eklendi
        }
    }
}
