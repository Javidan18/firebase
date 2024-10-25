package com.example.sagliktakipandroid;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, passwordEditText, repeatPasswordEditText;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference; // Realtime Database referansı

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase Authentication ve Database referanslarını al
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users"); // "Users" adında bir referans oluştur

        // UI elemanlarını tanımla
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText);

        // Kayıt olma butonu tıklama olayı
        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this::onRegisterButtonClick);

        // LoginActivity'ye geri dönme butonu tıklama olayı
        Button backToLoginButton = findViewById(R.id.backToLoginButton);
        backToLoginButton.setOnClickListener(this::onBackToLoginButtonClick);
    }

    // Kayıt olma butonuna tıklanıldığında çağrılacak fonksiyon
    public void onRegisterButtonClick(View view) {
        createUser();
    }

    // Geri LoginActivity'ye dönme butonu tıklanıldığında çağrılacak fonksiyon
    public void onBackToLoginButtonClick(View view) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Firebase üzerinden kullanıcı kaydı oluşturma ve veritabanına kaydetme fonksiyonu
    private void createUser() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String repeatPassword = repeatPasswordEditText.getText().toString().trim();

        // Kullanıcı adı, email ve şifre kontrolleri
        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Kullanıcı adı gerekli");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email gerekli");
            return;
        }

        if (!email.endsWith("@gmail.com")) {
            emailEditText.setError("Email '@gmail.com' ile bitmelidir");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Şifre gerekli");
            return;
        }

        if (TextUtils.isEmpty(repeatPassword)) {
            repeatPasswordEditText.setError("Şifreyi tekrar girin");
            return;
        }

        if (!password.equals(repeatPassword)) {
            repeatPasswordEditText.setError("Şifreler eşleşmiyor");
            return;
        }

        // Firebase Authentication ile yeni kullanıcı oluşturma
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Kullanıcı bilgilerini Realtime Database'e kaydet
                        if (user != null) {
                            String userId = user.getUid(); // Kullanıcının ID'sini al
                            User newUser = new User(username, email); // Kullanıcı bilgilerini içeren bir User nesnesi oluştur
                            databaseReference.child(userId).setValue(newUser) // Kullanıcı bilgilerini veritabanına ekle
                                    .addOnCompleteListener(databaseTask -> {
                                        if (databaseTask.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "Kayıt Başarılı", Toast.LENGTH_SHORT).show();
                                            // LoginActivity'ye geçiş yap
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Log.w("RegisterActivity", "Database error: " + databaseTask.getException());
                                            Toast.makeText(RegisterActivity.this, "Kullanıcı bilgileri kaydedilemedi.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Log.w("RegisterActivity", "createUserWithEmail:failure", task.getException());
                        if (task.getException() != null) {
                            String errorMessage = task.getException().getMessage();
                            if (errorMessage != null) {
                                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Kayıt başarısız oldu.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // User sınıfı
    public static class User {
        public String username;
        public String email;

        public User() {
            // Boş constructor gerekli
        }

        public User(String username, String email) {
            this.username = username;
            this.email = email;
        }
    }
}
