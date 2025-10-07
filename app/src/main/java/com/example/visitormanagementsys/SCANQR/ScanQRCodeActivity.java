package com.example.visitormanagementsys.SCANQR;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.visitormanagementsys.AddVisitors.ApiService;
import com.example.visitormanagementsys.AddVisitors.Department;
import com.example.visitormanagementsys.AddVisitors.DepartmentRequest;
import com.example.visitormanagementsys.AddVisitors.DepartmentResponse;
import com.example.visitormanagementsys.AddVisitors.Employee;
import com.example.visitormanagementsys.AddVisitors.EmployeeResponse;
import com.example.visitormanagementsys.ApiClient;   // ✅ important import added
import com.example.visitormanagementsys.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanQRCodeActivity extends AppCompatActivity {

    Spinner spinnerDepartment, spinnerEmployee;
    ApiService apiService;
    ImageView imgPhoto;
    Bitmap selectedImageBitmap;
    ActivityResultLauncher<Intent> cameraLauncher;
    private List<Department> departmentList = new ArrayList<>();
    private List<Employee> employeeList = new ArrayList<>();

    ProgressDialog progressDialog;
  //  private Button btnStartScan;
    private EditText etScannedName, etScannedEmail, etScannedAddress, etScannedPurpose, etScannedCompany, etScannedRaw;
    LinearLayout btnStartScan;
    // ✅ Modern permission launcher
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

        //btnStartScan = findViewById(R.id.btnStartScan);
        btnStartScan = findViewById(R.id.btnStartScan);
        etScannedName = findViewById(R.id.etScannedName);
        etScannedEmail = findViewById(R.id.etScannedEmail);
        etScannedAddress = findViewById(R.id.etScannedAddress);
        etScannedPurpose = findViewById(R.id.etScannedPurpose);
        etScannedCompany = findViewById(R.id.etScannedCompany);
        etScannedRaw = findViewById(R.id.etScannedRaw);
        spinnerDepartment = findViewById(R.id.spinnerDepartments);
        spinnerEmployee = findViewById(R.id.spinnerEmployees);
        imgPhoto = findViewById(R.id.imgPhotos);

        // ✅ Initialize Retrofit Service (this line is the main fix)
        apiService = ApiClient.getClient().create(ApiService.class);

        // ✅ Load departments initially
        DepartmentNameLoad();

        btnStartScan.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                startScanner();
            } else {
                requestCameraPermission();
            }
        });

        spinnerDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!departmentList.isEmpty()) {
                    Department selectedDept = departmentList.get(position);
                    int deptId = selectedDept.getDepartment_id();
                    loadEmployees(deptId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // ✅ Camera Result Launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        if (imageBitmap != null) {
                            selectedImageBitmap = imageBitmap;
                            imgPhoto.setImageBitmap(imageBitmap);
                        }
                    }
                });

        // ✅ ImageView click listener
        imgPhoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
            } else {
                openCamera();
            }
        });
    }

    // ✅ Camera Open Function
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    // ✅ Department List Load
    private void DepartmentNameLoad() {
        Call<DepartmentResponse> call = apiService.getDepartments();
        call.enqueue(new Callback<DepartmentResponse>() {
            @Override
            public void onResponse(Call<DepartmentResponse> call, Response<DepartmentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    departmentList = response.body().getDepartments();

                    List<String> departmentNames = new ArrayList<>();
                    for (Department dept : departmentList) {
                        departmentNames.add(dept.getDepartment_name());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            ScanQRCodeActivity.this,
                            android.R.layout.simple_spinner_item,
                            departmentNames
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerDepartment.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<DepartmentResponse> call, Throwable t) {
                Toast.makeText(ScanQRCodeActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ✅ Employee Load
    private void loadEmployees(int deptId) {
        Call<EmployeeResponse> call = apiService.getEmployees(new DepartmentRequest(deptId));

        call.enqueue(new Callback<EmployeeResponse>() {
            @Override
            public void onResponse(Call<EmployeeResponse> call, Response<EmployeeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    employeeList = response.body().getEmployees();

                    List<String> employeeNames = new ArrayList<>();
                    for (Employee emp : employeeList) {
                        employeeNames.add(emp.getEmployee_name());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            ScanQRCodeActivity.this,
                            android.R.layout.simple_spinner_item,
                            employeeNames
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerEmployee.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<EmployeeResponse> call, Throwable t) {
                Toast.makeText(ScanQRCodeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ✅ Check camera permission
    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    // ✅ Request camera permission
    private void requestCameraPermission() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
    }

    // ✅ Start ZXing scanner
    private void startScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan QR Code");
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.setCameraId(0);
        integrator.initiateScan();
    }

    // ✅ Handle scan result
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

    // ✅ Parse QR content and populate EditTexts
    private void parseAndPopulate(String content) {
        etScannedName.setText("");
        etScannedEmail.setText("");
        etScannedAddress.setText("");
        etScannedPurpose.setText("");
        etScannedCompany.setText("");
        etScannedRaw.setVisibility(View.GONE);

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
            etScannedRaw.setVisibility(View.VISIBLE);
            etScannedRaw.setText(content);
            Toast.makeText(this, "Scanned raw content (format not recognized).", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Scanned and populated fields.", Toast.LENGTH_SHORT).show();
        }
    }
}
