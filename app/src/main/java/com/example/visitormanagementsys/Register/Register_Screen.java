package com.example.visitormanagementsys.Register;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.visitormanagementsys.ApiClient;
import com.example.visitormanagementsys.Login.Login_Screen;
import com.example.visitormanagementsys.R;

import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register_Screen extends AppCompatActivity {

    TextInputEditText edtUsername, edtEmail, edtPassword, edtConfirmPassword;
    Button btnRegister;
    TextView txtLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        edtUsername = findViewById(R.id.edtUsernamer);
        edtEmail = findViewById(R.id.edtEmailr);
        edtPassword = findViewById(R.id.edtPasswordr);
        edtConfirmPassword = findViewById(R.id.edtConfirmPasswordr);
        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLoginr);

        txtLogin.setOnClickListener(view -> finish()); // back to login screen

        btnRegister.setOnClickListener(view -> {
            String username = edtUsername.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) ||
                    TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(Register_Screen.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(Register_Screen.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            RegisterRequest request = new RegisterRequest(username, email, password);
            registerUser(request);
        });
    }

    private void registerUser(RegisterRequest request) {
        RegisterApiService apiService = ApiClient.getClient().create(RegisterApiService.class);
        Call<RegisterResponse> call = apiService.registerUser(request);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(Register_Screen.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Register_Screen.this, Login_Screen.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(Register_Screen.this, "Failed to register", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(Register_Screen.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
