package com.example.visitormanagementsys;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingActivity extends AppCompatActivity {
    TextView tvProfileName, tvProfileEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);

        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String username = prefs.getString("username", "N/A");
        String email = prefs.getString("email", "N/A");

        tvProfileName.setText(username);
        tvProfileEmail.setText(email);


    }
}