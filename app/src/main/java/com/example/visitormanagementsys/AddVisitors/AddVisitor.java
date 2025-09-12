package com.example.visitormanagementsys.AddVisitors;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.visitormanagementsys.ApiClient;
import com.example.visitormanagementsys.R;

import android.Manifest;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddVisitor extends AppCompatActivity {

    EditText etName, etMobile, etAddress, etCompany, etPurpose;
    Spinner spinnerDepartment, spinnerEmployee;
    Button btnSubmit;

    ApiService apiService;
    ImageView imgPhoto;
    Bitmap selectedImageBitmap;
    ActivityResultLauncher<Intent> cameraLauncher;

    private List<Department> departmentList = new ArrayList<>();
    private List<Employee> employeeList = new ArrayList<>();

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_visitor);

        etName = findViewById(R.id.etName);
        etMobile = findViewById(R.id.etMobile);
        etAddress = findViewById(R.id.etAddress);
        etCompany = findViewById(R.id.etCompany);
        etPurpose = findViewById(R.id.etPurpose);
        spinnerDepartment = findViewById(R.id.spinnerDepartment);
        spinnerEmployee = findViewById(R.id.spinnerEmployee);
        btnSubmit = findViewById(R.id.btnSubmit);
        imgPhoto = findViewById(R.id.imgPhoto);

        apiService = ApiClient.getClient().create(ApiService.class);

        DepartmentNameLoad();




        // ✅ Button Click
        btnSubmit.setOnClickListener(view -> submitVisitor());

        // ✅ Spinner Listener
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

        // Inside onCreate()

// 👉 Name field me digits allow na ho
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();
                if (!input.matches("^[a-zA-Z ]*$")) {
                    etName.setError("Digits not allowed in name");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

// 👉 Mobile field me max 10 digit
        etMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();

                if (!input.matches("^[0-9]*$")) {
                    etMobile.setError("Only digits allowed");
                    return;
                }

                if (input.length() > 10) {
                    etMobile.setError("You can’t enter more than 10 digits");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

    }

    // ✅ Camera Open Function
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    // ✅ Permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    // ✅ Department Load
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
                            AddVisitor.this,
                            android.R.layout.simple_spinner_item,
                            departmentNames
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerDepartment.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<DepartmentResponse> call, Throwable t) {
                Toast.makeText(AddVisitor.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                            AddVisitor.this,
                            android.R.layout.simple_spinner_item,
                            employeeNames
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerEmployee.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<EmployeeResponse> call, Throwable t) {
                Toast.makeText(AddVisitor.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ✅ Bitmap -> Base64 Convert
    private String convertImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    // ✅ Submit Visitor
    private void submitVisitor() {
        if (!validateInputs()) {
            return; // Agar validation fail hua to API call hi mat karo
        }
        String name = etName.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String company = etCompany.getText().toString().trim();
        String purpose = etPurpose.getText().toString().trim();

        String department = spinnerDepartment.getSelectedItem().toString();
        String employee = spinnerEmployee.getSelectedItem() != null ? spinnerEmployee.getSelectedItem().toString() : "";

        // ✅ Email निकाल रहे हैं selected employee से
        String employeeEmail = "";
        if (!employeeList.isEmpty() && spinnerEmployee.getSelectedItemPosition() >= 0) {
            Employee selectedEmp = employeeList.get(spinnerEmployee.getSelectedItemPosition());
            employeeEmail = selectedEmp.getEmail();
        }


        // ✅ Photo Convert
        String photo = "";
        if (selectedImageBitmap != null) {
            photo = convertImageToBase64(selectedImageBitmap);
        }


        progressDialog = new ProgressDialog(AddVisitor.this);
        progressDialog.setMessage("Submitting visitor details...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        AddVisitorModel visitor = new AddVisitorModel(name, mobile, address, company, purpose, department, employee, photo,employeeEmail);

        Call<ResponseModel> call = apiService.addVisitor(visitor);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                  progressDialog.dismiss();
                    Toast.makeText(AddVisitor.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    clearForm();
                } else {
                    Toast.makeText(AddVisitor.this, "Failed to add visitor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Toast.makeText(AddVisitor.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ✅ Validation Method
    private boolean validateInputs() {
        String name = etName.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String company = etCompany.getText().toString().trim();
        String purpose = etPurpose.getText().toString().trim();

        // Name Validation (only alphabets and spaces)
        if (name.isEmpty()) {
            etName.setError("Please enter name");
            etName.requestFocus();
            return false;
        }
        if (!name.matches("^[a-zA-Z ]+$")) {
            etName.setError("Name cannot contain numbers or special characters");
            etName.requestFocus();
            return false;
        }

        // Mobile Validation (exactly 10 digits)
        if (mobile.isEmpty()) {
            etMobile.setError("Please enter mobile number");
            etMobile.requestFocus();
            return false;
        }
        if (!mobile.matches("^[0-9]{10}$")) {
            etMobile.setError("Mobile number must be exactly 10 digits");
            etMobile.requestFocus();
            return false;
        }

        // Address Validation
        if (address.isEmpty()) {
            etAddress.setError("Please enter address");
            etAddress.requestFocus();
            return false;
        }

        // Company Validation
        if (company.isEmpty()) {
            etCompany.setError("Please enter company");
            etCompany.requestFocus();
            return false;
        }

        // Purpose Validation
        if (purpose.isEmpty()) {
            etPurpose.setError("Please enter purpose");
            etPurpose.requestFocus();
            return false;
        }

        // Spinner Validation
        if (spinnerDepartment.getSelectedItem() == null ||
                spinnerDepartment.getSelectedItem().toString().isEmpty()) {
            Toast.makeText(this, "Please select department", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spinnerEmployee.getSelectedItem() == null ||
                spinnerEmployee.getSelectedItem().toString().isEmpty()) {
            Toast.makeText(this, "Please select employee", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Photo Validation
        if (selectedImageBitmap == null) {
            Toast.makeText(this, "Please capture photo", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true; // ✅ All fields valid
    }


    private void clearForm() {
        etName.setText("");
        etMobile.setText("");
        etAddress.setText("");
        etCompany.setText("");
        etPurpose.setText("");

        spinnerDepartment.setSelection(0); // first item select
        spinnerEmployee.setSelection(0);   // first item select

        imgPhoto.setImageResource(R.drawable.ic_camera); // default camera icon
        selectedImageBitmap = null;
    }
}
