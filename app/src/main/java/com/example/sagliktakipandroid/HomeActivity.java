package com.example.sagliktakipandroid;

import android.os.Bundle;
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
    }
}
