package com.example.sagliktakipandroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText emailEditText, codeEditText, newPasswordEditText;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // EditText ve Button bileşenlerini bul
        emailEditText = findViewById(R.id.emailEditText);
        codeEditText = findViewById(R.id.codeEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        Button submitButton = findViewById(R.id.submitButton);

        // Yüklenme göstergesi ayarları
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Şifre güncelleniyor...");
        progressDialog.setCancelable(false);

        // Submit butonuna tıklama olayını tanımlayın
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword(v); // Şifre sıfırlama işlemini başlat
            }
        });
    }

    // Şifre sıfırlama işlemi
    private void resetPassword(View view) {
        String email = emailEditText.getText().toString().trim();
        String code = codeEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();

        // Giriş doğrulamaları
        if (TextUtils.isEmpty(email)) {
            showError(emailEditText, "Lütfen e-posta adresinizi girin.");
            return;
        }
        if (!email.endsWith("@gmail.com")) {
            Snackbar.make(view, "Lütfen geçerli bir Gmail adresi girin.", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(code)) {
            showError(codeEditText, "Lütfen e-posta kodunu girin.");
            return;
        }
        if (TextUtils.isEmpty(newPassword)) {
            showError(newPasswordEditText, "Lütfen yeni şifrenizi girin.");
            return;
        }

        // Yüklenme göstergesini göster
        progressDialog.show();

        // Gerekli işlemleri burada yapın (örneğin, kod doğrulama, şifre güncelleme)
        simulatePasswordReset(email, code, newPassword, view);
    }

    // Hata mesajlarını gösteren yardımcı yöntem
    private void showError(EditText editText, String message) {
        editText.setError(message);
        editText.requestFocus(); // Hatalı alanı vurgula
    }

    // Şifre sıfırlama simülasyonu
    private void simulatePasswordReset(String email, String code, String newPassword, View view) {
        new android.os.Handler().postDelayed(() -> {
            // Yüklenme göstergesini gizle
            progressDialog.dismiss();

            // İşlem başarılıysa
            Toast.makeText(ResetPasswordActivity.this, "Şifre güncellendi", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // ResetPasswordActivity'yi kapat

        }, 2000); // 2 saniye sonra işlem tamamlandı
    }

    public void onBackButtonClick(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish(); // ResetPasswordActivity'yi kapat
    }
}
