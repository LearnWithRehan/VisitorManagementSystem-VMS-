package com.example.visitormanagementsys.Report;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visitormanagementsys.ApiClient;
import com.example.visitormanagementsys.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportActivity extends AppCompatActivity {

    private TextView tvFromDate, tvToDate;
    private Button btnShowReport;
    private RecyclerView recyclerReport;

    private List<ReportModel> visitorList = new ArrayList<>();
    private VisitorReportAdapter adapter;

    private String fromDateStr = "", toDateStr = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        tvFromDate = findViewById(R.id.tvFromDate);
        tvToDate = findViewById(R.id.tvToDate);
        btnShowReport = findViewById(R.id.btnGenerateReport);
        recyclerReport = findViewById(R.id.recyclerReport);
        LinearLayout layoutFromDate = findViewById(R.id.layoutFromDate);
        LinearLayout layoutToDate = findViewById(R.id.layoutToDate);

        recyclerReport.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VisitorReportAdapter(this, visitorList);
        recyclerReport.setAdapter(adapter);

     //   tvFromDate.setOnClickListener(v -> showDatePicker(true));
      //  tvToDate.setOnClickListener(v -> showDatePicker(false));


        layoutFromDate.setOnClickListener(v -> showDatePicker(true));
        layoutToDate.setOnClickListener(v -> showDatePicker(false));


        btnShowReport.setOnClickListener(v -> {
            if (fromDateStr.isEmpty() || toDateStr.isEmpty()) {
                Toast.makeText(this, "Please select both From and To dates", Toast.LENGTH_SHORT).show();
            } else {
                fetchReport(fromDateStr, toDateStr);
            }
        });
    }

    private void showDatePicker(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String dateStr = sdf.format(selected.getTime());

                    if (isFromDate) {
                        fromDateStr = dateStr;
                        tvFromDate.setText(dateStr);
                    } else {
                        toDateStr = dateStr;
                        tvToDate.setText(dateStr);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void fetchReport(String fromDate, String toDate) {
        ReportService service = ApiClient.getClient().create(ReportService.class);
        Call<VisitorReportResponse> call = service.getVisitorsReport(fromDate, toDate);

        call.enqueue(new Callback<VisitorReportResponse>() {
            @Override
            public void onResponse(Call<VisitorReportResponse> call, Response<VisitorReportResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        visitorList.clear();
                        visitorList.addAll(response.body().getData());
                        adapter.notifyDataSetChanged();

                        if (visitorList.isEmpty()) {
                            Toast.makeText(ReportActivity.this, "No records found for selected dates", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ReportActivity.this, "API returned failure", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ReportActivity.this, "Response failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VisitorReportResponse> call, Throwable t) {
                Toast.makeText(ReportActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
