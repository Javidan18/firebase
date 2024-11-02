package com.example.sagliktakipandroid;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText otpEditText1, otpEditText2, otpEditText3, otpEditText4;
    private Button verifyOtpButton;
    private TextView resendCodeTextView;
    private TextView timerTextView;
    private DatabaseReference userRef;

    private String username, email, password;
    private String sentOtpCode;

    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        userRef = FirebaseDatabase.getInstance().getReference("Users");

        // Intent verilerini al
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
        sentOtpCode = intent.getStringExtra("otpCode");

        // Null kontrolü
        if (sentOtpCode == null) {
            Toast.makeText(this, "OTP kodu alınamadı. Lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // UI elemanlarını tanımla
        otpEditText1 = findViewById(R.id.otpEditText1);
        otpEditText2 = findViewById(R.id.otpEditText2);
        otpEditText3 = findViewById(R.id.otpEditText3);
        otpEditText4 = findViewById(R.id.otpEditText4);
        verifyOtpButton = findViewById(R.id.verifyOtpButton);
        resendCodeTextView = findViewById(R.id.resendCodeTextView);
        timerTextView = findViewById(R.id.timerTextView);

        resendCodeTextView.setVisibility(View.GONE);
        resendCodeTextView.setEnabled(false);

        startCountDownTimer();

        // OTP doğrulama işlemi
        verifyOtpButton.setOnClickListener(v -> {
            String otpCode = otpEditText1.getText().toString() + otpEditText2.getText().toString() +
                    otpEditText3.getText().toString() + otpEditText4.getText().toString();

            if (otpCode.length() != 4) {
                Toast.makeText(OtpVerificationActivity.this, "Lütfen 4 haneli bir kod giriniz.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (otpCode.equals(sentOtpCode)) {
                saveUserToDatabase();
                startActivity(new Intent(OtpVerificationActivity.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(OtpVerificationActivity.this, "Yanlış OTP kodu. Lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
            }
        });

        // Kod yeniden gönderme işlemi
        resendCodeTextView.setOnClickListener(v -> resetCountDownTimer());
    }

    // Geri sayımı başlatır
    private void startCountDownTimer() {
        timerTextView.setVisibility(View.VISIBLE);
        timerTextView.setText("10");

        countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                resendCodeTextView.setVisibility(View.VISIBLE);
                resendCodeTextView.setEnabled(true);
                timerTextView.setVisibility(View.GONE);
                Toast.makeText(OtpVerificationActivity.this, "Kod yeniden gönderilebilir.", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

    // Geri sayımı sıfırlayıp yeniden başlatır
    private void resetCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        resendCodeTextView.setVisibility(View.GONE);
        resendCodeTextView.setEnabled(false);
        startCountDownTimer();
    }

    // Kullanıcı bilgilerini veritabanına kaydeder
    private void saveUserToDatabase() {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("username", username);
        userMap.put("email", email);
        userMap.put("password", password);

        String userId = userRef.push().getKey();

        if (userId != null) {
            userRef.child(userId).setValue(userMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(OtpVerificationActivity.this, "Kayıt başarılı!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OtpVerificationActivity.this, "Kayıt başarısız!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(OtpVerificationActivity.this, "Kullanıcı ID'si oluşturulamadı!", Toast.LENGTH_SHORT).show();
        }
    }
}
