package com.example.sagliktakipandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button; // Button kullanıyoruz
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    private String userId; // Kullanıcı ID'sini saklamak için değişken

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // activity_home.xml dosyasını ayarlayın

        // Kullanıcı ID'sini alın (örneğin, giriş yaptıktan sonra bu değeri alabilirsiniz)
        userId = getIntent().getStringExtra("USER_ID"); // USER_ID'yi almak için intent kullanın
    }

    // Butona tıklandığında çalışacak metod
    public void onSettingsButtonClick(View view) {
        Intent intent = new Intent(HomeActivity.this, SettingsHomeActivity.class);
        intent.putExtra("USER_ID", userId); // Kullanıcı ID'sini aktarın
        startActivity(intent);
    }
}
