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
    private EditText newPasswordEditText, repeatPasswordEditText;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // EditText ve Button bileşenlerini bul
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText);

        Button submitButton = findViewById(R.id.submitButton);
        Button backButton = findViewById(R.id.backButton); // Geri Dön butonu

        // Yüklenme göstergesi ayarları
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Şifre güncelleniyor...");
        progressDialog.setCancelable(false);

        // Submit butonuna tıklama olayını tanımlayın
        submitButton.setOnClickListener(v -> resetPassword(v)); // Şifre sıfırlama işlemini başlat

        // Geri Dön butonuna tıklama olayını tanımlayın
        backButton.setOnClickListener(v -> onBackButtonClick()); // Geri dönme işlemini başlat
    }

    // Şifre sıfırlama işlemi
    private void resetPassword(View view) {
        String newPassword = newPasswordEditText.getText().toString().trim();
        String repeatPassword = repeatPasswordEditText.getText().toString().trim();

        // Giriş doğrulamaları
        if (TextUtils.isEmpty(newPassword)) {
            showError(newPasswordEditText, "Lütfen yeni şifrenizi girin.");
            return;
        }
        if (TextUtils.isEmpty(repeatPassword)) {
            showError(repeatPasswordEditText, "Lütfen şifreyi tekrar girin.");
            return;
        }
        if (!newPassword.equals(repeatPassword)) {
            Snackbar.make(view, "Şifreler eşleşmiyor.", Snackbar.LENGTH_SHORT).show();
            return;
        }

        // Yüklenme göstergesini göster
        progressDialog.show();

        // Gerekli işlemleri burada yapın (örneğin, şifre güncelleme)
        simulatePasswordReset(newPassword, view);
    }

    // Hata mesajlarını gösteren yardımcı yöntem
    private void showError(EditText editText, String message) {
        editText.setError(message);
        editText.requestFocus(); // Hatalı alanı vurgula
    }

    // Şifre sıfırlama simülasyonu
    private void simulatePasswordReset(String newPassword, View view) {
        new android.os.Handler().postDelayed(() -> {
            // Yüklenme göstergesini gizle
            progressDialog.dismiss();

            // İşlem başarılıysa
            Toast.makeText(ResetPasswordActivity.this, "Şifre güncellendi", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
            finish(); // ResetPasswordActivity'yi kapat

        }, 2000); // 2 saniye sonra işlem tamamlandı
    }

    // Geri Dön butonuna tıklandığında çağrılan yöntem
    public void onBackButtonClick() {
        startActivity(new Intent(this, LoginActivity.class));
        finish(); // ResetPasswordActivity'yi kapat
    }
}
