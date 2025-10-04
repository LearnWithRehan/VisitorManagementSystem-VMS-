package com.example.visitormanagementsys;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.visitormanagementsys.ActiveVisit.ActiveVisitor;
import com.example.visitormanagementsys.AddVisitors.AddVisitor;
import com.example.visitormanagementsys.Login.Login_Screen;

public class HomeActivity extends AppCompatActivity {
    LinearLayout addVisitor, activeVisitors, historyReports, settingsProfile, qrGeneration, notification;
    Button gateCodeBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        addVisitor = findViewById(R.id.addVisitor);
        activeVisitors = findViewById(R.id.activeVisitors);
        historyReports = findViewById(R.id.historyReports);
        settingsProfile = findViewById(R.id.settingsProfile);
        qrGeneration = findViewById(R.id.qrGeneration);
        notification = findViewById(R.id.notification);
        gateCodeBtn = findViewById(R.id.gateCodeBtn);

        // Click Listeners
        addVisitor.setOnClickListener(v -> openScreen(AddVisitor.class));
        activeVisitors.setOnClickListener(v -> openScreen(ActiveVisitor.class));
        historyReports.setOnClickListener(v -> openScreen(ReportActivity.class));
        settingsProfile.setOnClickListener(v -> openScreen(SettingActivity.class));
        qrGeneration.setOnClickListener(v -> openScreen(GenerateQRCodeSCreen.class));
        notification.setOnClickListener(v -> openScreen(NotificationScreen.class));
        gateCodeBtn.setOnClickListener(v -> openScreen(GenerateQRCodeSCreen.class));


        //SharedPreferences prefs = getSharedPreferences("EmployeePrefs", MODE_PRIVATE);
      //  String employeeName = prefs.getString("employee_name", "Employee");




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(HomeActivity.this, Login_Screen.class);
        startActivity(intent);
    }

    private void openScreen(Class<?> cls) {
        Intent intent = new Intent(HomeActivity.this, cls);
        startActivity(intent);
    }
}