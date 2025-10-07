package com.example.visitormanagementsys.QRCODE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.visitormanagementsys.ApiClient;
import com.example.visitormanagementsys.HomeActivity;
import com.example.visitormanagementsys.MainActivity;
import com.example.visitormanagementsys.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenerateQRCodeSCreen extends AppCompatActivity {

    private EditText etName, etEmail, etAddress, etPurpose, etCompany;
    private Button btnGenerateQR;
    private ImageView imgQrCode;

    private QRApiService apiService;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qrcode_screen);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        etPurpose = findViewById(R.id.etPurpose);
        etCompany = findViewById(R.id.etCompany);
        btnGenerateQR = findViewById(R.id.btnGenerateQR);
        imgQrCode = findViewById(R.id.imgQrCode);

        apiService = ApiClient.getClient().create(QRApiService.class);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing, please wait...");
        progressDialog.setCancelable(false);

        btnGenerateQR.setOnClickListener(v -> generateAndSendQR());
    }

    private void generateAndSendQR() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String purpose = etPurpose.getText().toString().trim();
        String company = etCompany.getText().toString().trim();

        // ✅ Field validation
        if (name.isEmpty() || email.isEmpty() || address.isEmpty() || purpose.isEmpty() || company.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Email validation: must be proper Gmail address
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || !email.endsWith("@gmail.com")) {
            etEmail.setError("Enter a valid Gmail address");
            etEmail.requestFocus();
            return;
        }

        // Combine data for QR
        String qrData = "Name: " + name + "\nEmail: " + email + "\nAddress: " + address +
                "\nPurpose: " + purpose + "\nCompany: " + company;

        try {
            Bitmap qrBitmap = generateQRCode(qrData);
            imgQrCode.setImageBitmap(qrBitmap);
            imgQrCode.setVisibility(View.VISIBLE);

            String encodedQR = encodeToBase64(qrBitmap);
            sendDataToServer(name, email, address, purpose, company, encodedQR);

        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "QR generation failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap generateQRCode(String text) throws WriterException {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        BitMatrix matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 400, 400, hints);

        int width = matrix.getWidth();
        int height = matrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return bitmap;
    }

    private String encodeToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    private void sendDataToServer(String name, String email, String address, String purpose, String company, String encodedQR) {
        progressDialog.show();

        Call<QRApiResponse> call = apiService.sendVisitorDetails(name, email, address, purpose, company, encodedQR);

        call.enqueue(new Callback<QRApiResponse>() {
            @Override
            public void onResponse(Call<QRApiResponse> call, Response<QRApiResponse> response) {
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(GenerateQRCodeSCreen.this,
                            "✅ QR sent successfully! " + response.body().getMessage(),
                            Toast.LENGTH_LONG).show();

                    // 🔹 Navigate to Home screen after success
                    Intent intent = new Intent(GenerateQRCodeSCreen.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(GenerateQRCodeSCreen.this,
                            "❌ Failed: Invalid response",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<QRApiResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(GenerateQRCodeSCreen.this,
                        "⚠️ Error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
