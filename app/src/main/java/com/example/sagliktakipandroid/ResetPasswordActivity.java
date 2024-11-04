package com.example.sagliktakipandroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.example.sagliktakipandroid.domain.SendMailUseCase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText emailEditText, verificationCodeEditText, newPasswordEditText, repeatPasswordEditText;
    private ProgressDialog progressDialog;
    private SendMailUseCase sendMailUseCase;
    private String generatedOtpCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        emailEditText = findViewById(R.id.emailEditText);
        verificationCodeEditText = findViewById(R.id.verificationCodeEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText);

        Button submitButton = findViewById(R.id.submitButton);
        Button backButton = findViewById(R.id.backButton);
        TextView requestCodeText = findViewById(R.id.requestCodeText);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Şifre güncelleniyor...");
        progressDialog.setCancelable(false);

        submitButton.setOnClickListener(v -> resetPassword(v));
        backButton.setOnClickListener(v -> onBackButtonClick());
        requestCodeText.setOnClickListener(v -> requestVerificationCode(v));

        sendMailUseCase = new SendMailUseCase();
    }

    private void requestVerificationCode(View view) {
        String email = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            showError(emailEditText, "Lütfen e-posta adresinizi girin.");
            return;
        }

        progressDialog.show();
        // OTP kodunu oluştur ve gönder
        generatedOtpCode = sendMailUseCase.invoke(email);
        progressDialog.dismiss();

        if (generatedOtpCode != null) {
            Toast.makeText(this, "Doğrulama kodu e-posta adresinize gönderildi.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "E-posta gönderilirken bir hata oluştu.", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetPassword(View view) {
        String newPassword = newPasswordEditText.getText().toString().trim();
        String repeatPassword = repeatPasswordEditText.getText().toString().trim();
        String verificationCode = verificationCodeEditText.getText().toString().trim();

        if (TextUtils.isEmpty(verificationCode)) {
            showError(verificationCodeEditText, "Lütfen doğrulama kodunu girin.");
            return;
        }
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

        // OTP kodu kontrolü
        if (!verificationCode.equals(generatedOtpCode)) {
            Snackbar.make(view, "Doğrulama kodu hatalı.", Snackbar.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        updatePasswordInDatabase(newPassword, view);
    }

    private void updatePasswordInDatabase(String newPassword, View view) {
        String email = emailEditText.getText().toString().trim().toLowerCase(); // Küçük harfe çevir
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        Log.d("ResetPasswordActivity", "Kullanıcı arama için e-posta: " + email); // Debug için log

        databaseReference.orderByChild("email").equalTo(email).get().addOnCompleteListener(task -> {
            progressDialog.dismiss(); // Her durumda progressDialog'u kapat

            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    long count = task.getResult().getChildrenCount();
                    Log.d("ResetPasswordActivity", "Kullanıcı sayısı: " + count); // Kullanıcı sayısını log'la

                    if (count > 0) {
                        DataSnapshot userSnapshot = task.getResult().getChildren().iterator().next();
                        String userId = userSnapshot.getKey(); // Kullanıcı ID'sini al
                        Log.d("ResetPasswordActivity", "Kullanıcı ID'si bulundu: " + userId);

                        // Şifreyi güncelle
                        databaseReference.child(userId).child("password").setValue(newPassword)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Toast.makeText(ResetPasswordActivity.this, "Şifre güncellendi. Yeni şifreniz ile giriş yapın.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(ResetPasswordActivity.this, "Şifre güncellenirken hata oluştu.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Log.d("ResetPasswordActivity", "Hiçbir kullanıcı bulunamadı."); // Bu durumda kullanıcı bulunamamış
                        Snackbar.make(view, "Kullanıcı bulunamadı.", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("ResetPasswordActivity", "Sonuç null döndü."); // Sonucun null olması durumu
                    Snackbar.make(view, "Kullanıcı bulunamadı.", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Log.d("ResetPasswordActivity", "Sorgu başarısız. Hata: " + task.getException());
                Snackbar.make(view, "Bir hata oluştu.", Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    private void showError(EditText editText, String message) {
        editText.setError(message);
        editText.requestFocus();
    }

    public void onBackButtonClick() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
