package com.example.visitormanagementsys.Report;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visitormanagementsys.ApiClient;
import com.example.visitormanagementsys.R;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
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
    private Button btnShowReport, btnPdf;
    private RecyclerView recyclerReport;
    private List<ReportModel> visitorList = new ArrayList<>();
    private VisitorReportAdapter adapter;
    private String fromDateStr = "", toDateStr = "";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        tvFromDate = findViewById(R.id.tvFromDate);
        tvToDate = findViewById(R.id.tvToDate);
        btnShowReport = findViewById(R.id.btnGenerateReport);
        btnPdf = findViewById(R.id.btnGeneratePDF);
        recyclerReport = findViewById(R.id.recyclerReport);

        LinearLayout layoutFromDate = findViewById(R.id.layoutFromDate);
        LinearLayout layoutToDate = findViewById(R.id.layoutToDate);

        recyclerReport.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VisitorReportAdapter(this, visitorList);
        recyclerReport.setAdapter(adapter);

        layoutFromDate.setOnClickListener(v -> showDatePicker(true));
        layoutToDate.setOnClickListener(v -> showDatePicker(false));

        btnShowReport.setOnClickListener(v -> {
            if (fromDateStr.isEmpty() || toDateStr.isEmpty()) {
                Toast.makeText(this, "Please select both From and To dates", Toast.LENGTH_SHORT).show();
            } else {
                fetchReport(fromDateStr, toDateStr);
            }
        });

        btnPdf.setOnClickListener(v -> {
            if (visitorList.isEmpty()) {
                Toast.makeText(this, "No data to generate PDF", Toast.LENGTH_SHORT).show();
                return;
            }
            generatePdf(visitorList);
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
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    visitorList.clear();
                    visitorList.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ReportActivity.this, "No records found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VisitorReportResponse> call, Throwable t) {
                Toast.makeText(ReportActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generatePdf(List<ReportModel> visitorList) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Generating PDF... Please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(() -> {
            try {
                // Safe storage location
                File folder = new File(getExternalFilesDir(null), "VisitorReports");
                if (!folder.exists()) folder.mkdirs();

                String fileName = "VisitorReport_" + System.currentTimeMillis() + ".pdf";
                File file = new File(folder, fileName);

                try (OutputStream out = new FileOutputStream(file)) {
                    PdfWriter writer = new PdfWriter(out);
                    PdfDocument pdfDocument = new PdfDocument(writer);
                    Document document = new Document(pdfDocument, PageSize.A4.rotate());
                    document.setMargins(20, 20, 20, 20);

                    Paragraph title = new Paragraph("VISITORS REPORT")
                            .setFontSize(20)
                            .setBold()
                            .setTextAlignment(TextAlignment.CENTER)
                            .setMarginBottom(15);
                    document.add(title);

                    float[] columnWidths = {50, 100, 150, 80, 80, 100, 100, 100, 100, 100};
                    Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

                    String[] headers = {"ID", "Name", "Address", "Mobile", "Dept", "Employee", "Purpose", "Company", "In Date", "Out Date"};
                    for (String header : headers) {
                        table.addHeaderCell(new Cell().add(new Paragraph(header))
                                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                                .setBold()
                                .setTextAlignment(TextAlignment.CENTER));
                    }

                    for (ReportModel v : visitorList) {
                        table.addCell(new Cell().add(new Paragraph(v.getVisitor_id() != null ? v.getVisitor_id() : "")));
                        table.addCell(new Cell().add(new Paragraph(v.getName() != null ? v.getName() : "")));
                        table.addCell(new Cell().add(new Paragraph(v.getAddress() != null ? v.getAddress() : "")));
                        table.addCell(new Cell().add(new Paragraph(v.getMobile() != null ? v.getMobile() : "")));
                        table.addCell(new Cell().add(new Paragraph(v.getDepartment() != null ? v.getDepartment() : "")));
                        table.addCell(new Cell().add(new Paragraph(v.getEmployee() != null ? v.getEmployee() : "")));
                        table.addCell(new Cell().add(new Paragraph(v.getPurpose() != null ? v.getPurpose() : "")));
                        table.addCell(new Cell().add(new Paragraph(v.getCompany() != null ? v.getCompany() : "")));
                        table.addCell(new Cell().add(new Paragraph(v.getEntry_Date() != null ? v.getEntry_Date() : "")));
                        table.addCell(new Cell().add(new Paragraph(v.getOut_Date() != null ? v.getOut_Date() : "")));
                    }

                    document.add(table);
                    document.close();
                    out.flush();
                }

                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "PDF saved in app folder: VisitorReports/" + fileName, Toast.LENGTH_LONG).show();
                    openPdf(file);
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Error generating PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void openPdf(File file) {
        try {
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No PDF viewer found", Toast.LENGTH_SHORT).show();
        }
    }
}
