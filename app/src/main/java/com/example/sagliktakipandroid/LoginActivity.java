package com.example.sagliktakipandroid;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private FirebaseAuth mAuth;

    public void onRegisterButtonClick(View view) {
        // RegisterActivity'ye geçiş yap
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void onForgotPasswordClick(View view) {
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase Authentication örneğini al
        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        // Giriş butonuna tıklanma olayını tanımla
        loginButton.setOnClickListener(v -> loginUser());
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

        // Firebase ile kullanıcı girişi yap
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Giriş başarılı
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(LoginActivity.this, "Giriş Başarılı", Toast.LENGTH_SHORT).show();
                        // HomeActivity'ye yönlendir
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish(); // LoginActivity'yi kapat
                    } else {
                        // Giriş başarısız
                        Exception exception = task.getException();
                        String errorMessage = exception != null ? exception.getMessage() : "Giriş başarısız oldu.";
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
