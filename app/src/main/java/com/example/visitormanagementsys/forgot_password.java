package com.example.visitormanagementsys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.visitormanagementsys.Login.Login_Screen;

public class forgot_password extends AppCompatActivity {
TextView txtBackToLoginforgot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        txtBackToLoginforgot = findViewById(R.id.txtBackToLoginforgot);

        txtBackToLoginforgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(forgot_password.this, Login_Screen.class);
                startActivity(intent);
            }
        });
    }
}