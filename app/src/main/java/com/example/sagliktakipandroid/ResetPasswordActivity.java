package com.example.sagliktakipandroid;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils; // TextUtils ekleyin
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText emailEditText, codeEditText, newPasswordEditText;

    public void onBackButtonClick(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish(); // ResetPasswordActivity'yi kapat
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        emailEditText = findViewById(R.id.emailEditText);
        codeEditText = findViewById(R.id.codeEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        Button submitButton = findViewById(R.id.submitButton);

        // Kod butonuna tıklama olayını tanımlayın
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String code = codeEditText.getText().toString().trim();
                String newPassword = newPasswordEditText.getText().toString().trim();

                // E-posta kontrolü
                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("Lütfen e-posta adresinizi girin."); // Boş ise hata mesajı
                    return; // Eğer e-posta boşsa işlemi durdur
                }

                if (!email.endsWith("@gmail.com")) {
                    Toast.makeText(ResetPasswordActivity.this, "Lütfen geçerli bir Gmail adresi girin.", Toast.LENGTH_SHORT).show();
                    return; // Eğer e-posta geçerli değilse işlemi durdur
                }

                // Gerekli işlemleri burada yapın (örneğin, kod doğrulama, şifre güncelleme)
                // Burada kod doğrulama ve şifre güncelleme işlemini gerçekleştirin

                // Eğer işlemler başarılıysa kullanıcıyı LoginActivity'ye yönlendirin
                Toast.makeText(ResetPasswordActivity.this, "Şifre güncellendi", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // ResetPasswordActivity'yi kapat
            }
        });
    }
}
