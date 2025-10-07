package com.example.visitormanagementsys.SCANQR;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.visitormanagementsys.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanQRCodeActivity extends AppCompatActivity {

    private Button btnStartScan;
    private EditText etScannedName, etScannedEmail, etScannedAddress, etScannedPurpose, etScannedCompany, etScannedRaw;

    // Modern permission launcher
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startScanner();
                } else {
                    Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);

        btnStartScan = findViewById(R.id.btnStartScan);
        etScannedName = findViewById(R.id.etScannedName);
        etScannedEmail = findViewById(R.id.etScannedEmail);
        etScannedAddress = findViewById(R.id.etScannedAddress);
        etScannedPurpose = findViewById(R.id.etScannedPurpose);
        etScannedCompany = findViewById(R.id.etScannedCompany);
        etScannedRaw = findViewById(R.id.etScannedRaw);

        btnStartScan.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                startScanner();
            } else {
                requestCameraPermission();
            }
        });
    }

    // Check camera permission
    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    // Request camera permission
    private void requestCameraPermission() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
    }

    // Start ZXing scanner
    private void startScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE); // QR only
        integrator.setPrompt("Scan QR Code");
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.setCameraId(0); // back camera
        integrator.initiateScan();
    }

    // Handle scan result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();
            } else {
                parseAndPopulate(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Parse QR content and populate EditTexts
    private void parseAndPopulate(String content) {
        // Reset fields
        etScannedName.setText("");
        etScannedEmail.setText("");
        etScannedAddress.setText("");
        etScannedPurpose.setText("");
        etScannedCompany.setText("");
        etScannedRaw.setVisibility(android.view.View.GONE);

        boolean parsedAny = false;
        String[] lines = content.split("\\r?\\n");
        for (String line : lines) {
            if (!line.contains(":")) continue;
            String[] parts = line.split(":", 2);
            String key = parts[0].trim().toLowerCase();
            String value = parts.length > 1 ? parts[1].trim() : "";

            switch (key) {
                case "name":
                    etScannedName.setText(value);
                    parsedAny = true;
                    break;
                case "email":
                    etScannedEmail.setText(value);
                    parsedAny = true;
                    break;
                case "address":
                    etScannedAddress.setText(value);
                    parsedAny = true;
                    break;
                case "purpose":
                    etScannedPurpose.setText(value);
                    parsedAny = true;
                    break;
                case "company":
                    etScannedCompany.setText(value);
                    parsedAny = true;
                    break;
                default:
                    break;
            }
        }

        if (!parsedAny) {
            etScannedRaw.setVisibility(android.view.View.VISIBLE);
            etScannedRaw.setText(content);
            Toast.makeText(this, "Scanned raw content (format not recognized).", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Scanned and populated fields.", Toast.LENGTH_SHORT).show();
        }
    }

}
