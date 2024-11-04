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

import com.example.sagliktakipandroid.core_utils.MailAPI;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Random;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText otpEditText1, otpEditText2, otpEditText3, otpEditText4;
    private Button verifyOtpButton, backButton; // backButton burada tanımlandı
    private TextView resendCodeTextView, timerTextView;
    private DatabaseReference userRef;

    private String username, email, password, sentOtpCode;
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
        backButton = findViewById(R.id.backButton); // backButton referansı alındı

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
                checkEmailAndSaveUser();
            } else {
                Toast.makeText(OtpVerificationActivity.this, "Yanlış OTP kodu. Lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
            }
        });

        // Kod yeniden gönderme işlemi
        resendCodeTextView.setOnClickListener(v -> {
            resetCountDownTimer();
            sendNewOtpCode();
        });

        // Geri Dön butonuna tıklandığında RegisterActivity'ye geçiş yap
        backButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(OtpVerificationActivity.this, RegisterActivity.class);
            startActivity(backIntent);
            finish(); // OtpVerificationActivity'yi kapat
        });
    }

    // Yeni bir OTP kodu oluşturur ve gönderir
    private void sendNewOtpCode() {
        sentOtpCode = generateOtpCode();
        String subject = "Yeni OTP Kodunuz";
        String message = "Yeni OTP Kodunuz: ";

        // Yeni OTP kodunu gönder
        new MailAPI(email, subject, message, sentOtpCode).execute();
        Toast.makeText(this, "Yeni OTP kodu gönderildi.", Toast.LENGTH_SHORT).show();
    }

    // Rastgele bir OTP kodu oluşturur
    private String generateOtpCode() {
        Random random = new Random();
        int otp = 1000 + random.nextInt(9000); // 4 haneli bir kod oluşturur
        return String.valueOf(otp);
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
                Toast.makeText(OtpVerificationActivity.this, "", Toast.LENGTH_SHORT).show();
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

    // Aynı e-posta ile kayıt olup olmadığını kontrol eder ve kayıt işlemini yapar
    private void checkEmailAndSaveUser() {
        userRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Aynı e-posta zaten kayıtlıysa hata mesajı göster
                    Toast.makeText(OtpVerificationActivity.this, "Bu e-posta adresi zaten kayıtlı!", Toast.LENGTH_SHORT).show();
                } else {
                    // E-posta mevcut değilse yeni kullanıcıyı kaydet
                    HashMap<String, Object> userMap = new HashMap<>();
                    userMap.put("username", username);
                    userMap.put("email", email);
                    userMap.put("password", password);

                    String userId = userRef.push().getKey();

                    if (userId != null) {
                        userRef.child(userId).setValue(userMap).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(OtpVerificationActivity.this, "Kayıt başarılı!", Toast.LENGTH_SHORT).show();

                                // E-posta gönderimi
                                String subject = "Kayıt Başarılı";
                                String messageBody = "Sağlık Takip uygulamasına kaydınız başarıyla oluşturuldu. Hoş geldiniz!";
                                new MailAPI(email, subject, messageBody, "-1").execute(); // E-posta gönder

                                // Kullanıcı ID'sini LoginActivity'ye gönder
                                Intent loginIntent = new Intent(OtpVerificationActivity.this, LoginActivity.class);
                                loginIntent.putExtra("USER_ID", userId); // Kullanıcı ID'sini ekle
                                startActivity(loginIntent);
                                finish();
                            } else {
                                Toast.makeText(OtpVerificationActivity.this, "Kayıt başarısız!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(OtpVerificationActivity.this, "Kullanıcı ID'si oluşturulamadı!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(OtpVerificationActivity.this, "Veritabanı hatası: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
