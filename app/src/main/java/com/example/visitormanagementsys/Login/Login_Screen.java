package com.example.visitormanagementsys.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.visitormanagementsys.ApiClient;
import com.example.visitormanagementsys.HomeActivity;
import com.example.visitormanagementsys.MainActivity;
import com.example.visitormanagementsys.R;
import com.example.visitormanagementsys.Register.Register_Screen;

import com.example.visitormanagementsys.forgot_password;

import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login_Screen extends AppCompatActivity {

    TextInputEditText edtUsername, edtPassword;
    Button btnLogin;
    TextView txtRegister, forgotPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        edtUsername = findViewById(R.id.edtUsernamel);
        edtPassword = findViewById(R.id.edtPasswordl);
        btnLogin = findViewById(R.id.btnLogin);

        txtRegister = findViewById(R.id.txtRegisterl);
        forgotPass = findViewById(R.id.txtForgotPasswordl);

        // Navigate to Forgot Password
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login_Screen.this, forgot_password.class));
            }
        });

        // Navigate to Register Screen
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login_Screen.this, Register_Screen.class));
            }
        });

        // Login button click
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(Login_Screen.this, "Username and Password required", Toast.LENGTH_SHORT).show();
                    return;
                }

                loginUser(username, password);
            }
        });
    }

    private void loginUser(String username, String password) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<LoginResponse> call = apiService.loginUser(username, password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isStatus()) {
                        Toast.makeText(Login_Screen.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        // Move to home or dashboard activity
                         Intent intent = new Intent(Login_Screen.this, HomeActivity.class);
                         startActivity(intent);
                         finish();
                    } else {
                        Toast.makeText(Login_Screen.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Login_Screen.this, "Login failed! Try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(Login_Screen.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
