package com.example.sagliktakipandroid;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private DatabaseReference databaseReference; // Realtime Database referansı

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Database referansını al
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Görünüm öğelerini tanımla
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        // Giriş butonuna tıklanma olayını tanımla
        loginButton.setOnClickListener(v -> loginUser());
    }

    public void onRegisterButtonClick(View view) {
        // RegisterActivity'ye geçiş yap
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void onForgotPasswordClick(View view) {
        // ResetPasswordActivity'ye geçiş yap
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        startActivity(intent);
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Email ve şifre alanlarının boş olup olmadığını kontrol et
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email gerekli");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Şifre gerekli");
            return;
        }

        // Kullanıcı bilgilerini Realtime Database'den kontrol et
        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Kullanıcı bulundu
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        if (user != null && user.password.equals(password)) {
                            // Giriş başarılı
                            Toast.makeText(LoginActivity.this, "Giriş Başarılı", Toast.LENGTH_SHORT).show();

                            // Kullanıcı ID'sini al ve HomeActivity'ye geçir
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra("USER_ID", userSnapshot.getKey());
                            startActivity(intent);
                            finish();
                            return;
                        }
                    }
                    // Şifre yanlış
                    Toast.makeText(LoginActivity.this, "Şifre yanlış", Toast.LENGTH_SHORT).show();
                } else {
                    // Kullanıcı bulunamadı (kayıtlı e-posta yok)
                    Toast.makeText(LoginActivity.this, "Bu e-posta ile kayıtlı bir kullanıcı bulunamadı", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Veritabanı hatası: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
