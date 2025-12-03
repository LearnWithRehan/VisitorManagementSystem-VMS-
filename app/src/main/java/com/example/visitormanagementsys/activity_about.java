package com.example.visitormanagementsys;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class activity_about extends AppCompatActivity {
    TextView tvAboutTitle, tvAboutContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        tvAboutTitle = findViewById(R.id.tvAboutTitle);
        tvAboutContent = findViewById(R.id.tvAboutContent);

        tvAboutTitle.setText("About VMS Application");

    }
}