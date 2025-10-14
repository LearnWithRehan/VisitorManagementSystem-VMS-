package com.example.visitormanagementsys.Report;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

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

    private void generatePdf(List<InVisitor> visitorList) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Generating PDF...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(() -> {
            try {
                File folder = new File(getExternalFilesDir(null), "InVisitorReports");
                if (!folder.exists()) folder.mkdirs();

                String fileName = "InVisitorReport_" + System.currentTimeMillis() + ".pdf";
                File pdfFile = new File(folder, fileName);

                try (OutputStream out = new FileOutputStream(pdfFile)) {
                    createPdf(visitorList, out);
                }

                Uri pdfUri = FileProvider.getUriForFile(this,
                        getPackageName() + ".provider", pdfFile);

                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "PDF generated successfully", Toast.LENGTH_LONG).show();
                    openPdfUri(pdfUri);
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

    private void createPdf(List<InVisitor> visitorList, OutputStream outputStream) throws Exception {
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument, PageSize.A4);
        document.setMargins(20, 20, 20, 20);

        Paragraph title = new Paragraph("IN VISITORS REPORT")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(title);

        float[] columnWidths = {60, 100, 100, 100, 100, 100, 80, 80, 80, 80};
        Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

        String[] headers = {"Visitor ID", "Name", "Mobile", "Address", "Company", "Purpose", "Department", "Employee", "Status", "Entry Date"};
        for (String header : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(header))
                    .setBold()
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
        }

        for (InVisitor visitor : visitorList) {
            table.addCell(new Cell().add(new Paragraph(visitor.getVisitorId() != null ? visitor.getVisitorId() : "")));
            table.addCell(new Cell().add(new Paragraph(visitor.getName() != null ? visitor.getName() : "")));
            table.addCell(new Cell().add(new Paragraph(visitor.getMobile() != null ? visitor.getMobile() : "")));
            table.addCell(new Cell().add(new Paragraph(visitor.getAddress() != null ? visitor.getAddress() : "")));
            table.addCell(new Cell().add(new Paragraph(visitor.getCompany() != null ? visitor.getCompany() : "")));
            table.addCell(new Cell().add(new Paragraph(visitor.getPurpose() != null ? visitor.getPurpose() : "")));
            table.addCell(new Cell().add(new Paragraph(visitor.getDepartment() != null ? visitor.getDepartment() : "")));
            table.addCell(new Cell().add(new Paragraph(visitor.getEmployee() != null ? visitor.getEmployee() : "")));
            table.addCell(new Cell().add(new Paragraph(visitor.getStatus() != null ? visitor.getStatus() : "")));
            table.addCell(new Cell().add(new Paragraph(visitor.getEntryDate() != null ? visitor.getEntryDate() : "")));
        }

        document.add(table);
        document.close();
        outputStream.flush();
    }

    private void openPdfUri(Uri uri) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Open PDF with"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No PDF viewer app found", Toast.LENGTH_LONG).show();
        }
    }
}
