package com.example.visitormanagementsys.Report;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.visitormanagementsys.R;

public class Report_Selection extends AppCompatActivity {
    Button btnInReport, btnOutReport;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_selection);

        btnInReport = findViewById(R.id.btnInReport);
        btnOutReport = findViewById(R.id.btnOutReport);

        btnInReport.setOnClickListener(v -> {
            Intent intent = new Intent(Report_Selection.this, InReportActivity.class);
            startActivity(intent);
        });

        btnOutReport.setOnClickListener(v -> {
            Intent intent = new Intent(Report_Selection.this, ReportActivity.class); // Out report activity
            startActivity(intent);
        });

    }
}