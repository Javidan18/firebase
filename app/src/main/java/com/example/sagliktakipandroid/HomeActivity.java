package com.example.sagliktakipandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private TextView welcomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // activity_home.xml dosyasını ayarlayın

        // welcomeTextView'i bul
        welcomeTextView = findViewById(R.id.welcomeTextView);

        // Geri dönme butonunu bul


    }

}
