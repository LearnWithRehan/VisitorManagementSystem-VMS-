package com.example.visitormanagementsys.Report;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InReportActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Button btnGeneratePdf;
    private InVisitorAdapter adapter;
    private List<InVisitor> visitorList;

    private static final int STORAGE_PERMISSION_CODE = 101;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_report);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        btnGeneratePdf = findViewById(R.id.btnInGeneratePdf);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadInVisitors();

        btnGeneratePdf.setOnClickListener(v -> {
            if (visitorList == null || visitorList.isEmpty()) {
                Toast.makeText(this, "No data to generate PDF", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        return;
                    }
                }
            }

            generatePdf(visitorList);
        });
    }

    private void loadInVisitors() {
        progressBar.setVisibility(ProgressBar.VISIBLE);

        InVisitorService apiService = ApiClient.getClient().create(InVisitorService.class);
        Call<InVisitorResponse> call = apiService.getInVisitors();

        call.enqueue(new Callback<InVisitorResponse>() {
            @Override
            public void onResponse(Call<InVisitorResponse> call, Response<InVisitorResponse> response) {
                progressBar.setVisibility(ProgressBar.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    visitorList = response.body().getData();
                    adapter = new InVisitorAdapter(visitorList);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(InReportActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InVisitorResponse> call, Throwable t) {
                progressBar.setVisibility(ProgressBar.GONE);
                Toast.makeText(InReportActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void generatePdf(List<InVisitor> visitorList) {
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Generating PDF...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//
//        new Thread(() -> {
//            try {
//                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//                File folder = new File(downloadsDir, "InVisitorReports");
//                if (!folder.exists()) folder.mkdirs();
//
//                String fileName = "InVisitorReport_" + System.currentTimeMillis() + ".pdf";
//                File file = new File(folder, fileName);
//
//                FileOutputStream outputStream = new FileOutputStream(file);
//                PdfWriter writer = new PdfWriter(outputStream);
//                PdfDocument pdfDocument = new PdfDocument(writer);
//                Document document = new Document(pdfDocument, PageSize.A4.rotate());
//                document.setMargins(20, 20, 20, 20);
//
//                Paragraph title = new Paragraph("IN VISITORS REPORT")
//                        .setFontSize(20)
//                        .setBold()
//                        .setTextAlignment(TextAlignment.CENTER)
//                        .setMarginBottom(20);
//                document.add(title);
//
//                float[] columnWidths = {50, 100, 150, 80, 80, 100, 100, 100, 100, 100};
//                Table table = new Table(UnitValue.createPercentArray(columnWidths)).setWidth(UnitValue.createPercentValue(100));
//
//                String[] headers = {"ID", "Name", "Address", "Mobile", "Department", "Employee", "Purpose", "Company", "Entry Date", "Status"};
//                for (String header : headers) {
//                    table.addHeaderCell(new Cell().add(new Paragraph(header))
//                            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
//                            .setBold()
//                            .setTextAlignment(TextAlignment.CENTER));
//                }
//
//                for (InVisitor visitor : visitorList) {
//                    table.addCell(new Paragraph(visitor.getVisitorId()));
//                    table.addCell(new Paragraph(visitor.getName()));
//                    table.addCell(new Paragraph(visitor.getAddress()));
//                    table.addCell(new Paragraph(visitor.getMobile()));
//                    table.addCell(new Paragraph(visitor.getDepartment()));
//                    table.addCell(new Paragraph(visitor.getEmployee()));
//                    table.addCell(new Paragraph(visitor.getPurpose()));
//                    table.addCell(new Paragraph(visitor.getCompany()));
//                    table.addCell(new Paragraph(visitor.getEntryDate()));
//                    table.addCell(new Paragraph(visitor.getStatus()));
//                }
//
//                document.add(table);
//                document.close();
//                outputStream.close();
//
//                runOnUiThread(() -> {
//                    progressDialog.dismiss();
//                    Toast.makeText(this, "PDF saved: Downloads/InVisitorReports/" + fileName, Toast.LENGTH_LONG).show();
//                    openPdf(file);
//                });
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                runOnUiThread(() -> {
//                    progressDialog.dismiss();
//                    Toast.makeText(this, "Error generating PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                });
//            }
//        }).start();
//    }

//    private void openPdf(File file) {
//        try {
//            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(uri, "application/pdf");
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            startActivity(intent);
//        } catch (Exception e) {
//            Toast.makeText(this, "Cannot open PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }


    private void generatePdf(List<InVisitor> visitorList) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Generating PDF...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(() -> {
            try {
                // ✅ Folder setup
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File folder = new File(downloadsDir, "InVisitorReports");
                if (!folder.exists()) folder.mkdirs();

                String fileName = "InVisitorReport_" + System.currentTimeMillis() + ".pdf";
                File file = new File(folder, fileName);

                // ✅ PDF creation (using iText7 / OpenPDF)
                PdfWriter writer = new PdfWriter(new FileOutputStream(file));
                PdfDocument pdfDocument = new PdfDocument(writer);
                Document document = new Document(pdfDocument, PageSize.A4.rotate());
                document.setMargins(20, 20, 20, 20);

                // ✅ Title
                Paragraph title = new Paragraph("IN VISITORS REPORT")
                        .setFontSize(20)
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(20);
                document.add(title);

                // ✅ Table setup
                float[] columnWidths = {50, 100, 150, 80, 80, 100, 100, 100, 100, 100};
                Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

                String[] headers = {"ID", "Name", "Address", "Mobile", "Department", "Employee", "Purpose", "Company", "Entry Date", "Status"};
                for (String header : headers) {
                    table.addHeaderCell(new Cell()
                            .add(new Paragraph(header))
                            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                            .setBold()
                            .setTextAlignment(TextAlignment.CENTER));
                }

                // ✅ Data rows
                for (InVisitor visitor : visitorList) {
                    table.addCell(new Paragraph(visitor.getVisitorId()));
                    table.addCell(new Paragraph(visitor.getName()));
                    table.addCell(new Paragraph(visitor.getAddress()));
                    table.addCell(new Paragraph(visitor.getMobile()));
                    table.addCell(new Paragraph(visitor.getDepartment()));
                    table.addCell(new Paragraph(visitor.getEmployee()));
                    table.addCell(new Paragraph(visitor.getPurpose()));
                    table.addCell(new Paragraph(visitor.getCompany()));
                    table.addCell(new Paragraph(visitor.getEntryDate()));
                    table.addCell(new Paragraph(visitor.getStatus()));
                }

                document.add(table);
                document.close();

                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "PDF saved: Downloads/InVisitorReports/" + fileName, Toast.LENGTH_LONG).show();
                    openPdf(file);
                });

            } catch (Exception e) {
                e.printStackTrace();
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
            Toast.makeText(this, "No PDF viewer found", Toast.LENGTH_LONG).show();
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (visitorList != null && !visitorList.isEmpty()) {
                    generatePdf(visitorList);
                }
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
