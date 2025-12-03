package com.example.visitormanagementsys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.visitormanagementsys.Login.Login_Screen;

public class Splash_Screen extends AppCompatActivity {
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        progressBar = findViewById(R.id.progressBar);
        ImageView logo = findViewById(R.id.imgLogo);

        // Logo animation
        logo.setScaleX(0f);
        logo.setScaleY(0f);
        logo.animate().scaleX(1f).scaleY(1f).setDuration(1000).start();

        // Progress bar update in background
        new Thread(() -> {
            while (progressStatus < 100) {
                progressStatus += 2;
                handler.post(() -> progressBar.setProgress(progressStatus));
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Move to Login Screen
            Intent i = new Intent(Splash_Screen.this, Login_Screen.class);
            startActivity(i);
            finish();
        }).start();
    }
}
