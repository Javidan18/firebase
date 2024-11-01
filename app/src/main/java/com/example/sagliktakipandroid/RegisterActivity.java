package com.example.sagliktakipandroid;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sagliktakipandroid.domain.SendMailUseCase;


public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, passwordEditText, repeatPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // UI elemanlarını tanımla
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText);

        // Kayıt olma butonu tıklama olayı
        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this::onRegisterButtonClick);

        // Giriş sayfasına geri dönme butonu tıklama olayı
        Button backToLoginButton = findViewById(R.id.backToLoginButton);
        backToLoginButton.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish(); // Bu activity'yi kapat
        });
    }

    public void onRegisterButtonClick(View view) {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String repeatPassword = repeatPasswordEditText.getText().toString().trim();

        // Kullanıcı girişi doğrulama (örneğin, boş alan kontrolü)
        if (isValidInput(username, email, password, repeatPassword)) {
            // OTP kodunu oluştur ve gönder
            SendMailUseCase sendMailUseCase = new SendMailUseCase();
            sendMailUseCase.invoke(email, otpCode -> {
                // OTP kodunu aldığınızda, OtpVerificationActivity'ye geçiş yapın
                Intent intent = new Intent(RegisterActivity.this, OtpVerificationActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("email", email);
                intent.putExtra("password", password);
                intent.putExtra("otpCode",otpCode);  // OTP kodunu intent ile aktar
                startActivity(intent);
                finish();
            });
        }
    }
    private boolean isValidInput(String username, String email, String password, String repeatPassword) {
        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Kullanıcı adı gerekli");
            return false;
        }

        // Herhangi bir e-posta adresini kabul etmek için güncelleme
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Geçerli bir e-posta adresi girin");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Şifre gerekli");
            return false;
        }

        if (TextUtils.isEmpty(repeatPassword)) {
            repeatPasswordEditText.setError("Şifreyi tekrar girin");
            return false;
        }

        if (!password.equals(repeatPassword)) {
            repeatPasswordEditText.setError("Şifreler eşleşmiyor");
            return false;
        }

        return true;
    }


}
