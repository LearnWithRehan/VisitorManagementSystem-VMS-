package com.example.visitormanagementsys.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.visitormanagementsys.ApiClient;

import com.example.visitormanagementsys.HomeActivity;
import com.example.visitormanagementsys.R;
import com.example.visitormanagementsys.Register.Register_Screen;
import com.example.visitormanagementsys.forgot_password;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login_Screen extends AppCompatActivity {

    TextInputEditText edtUsername, edtPassword;
    Button btnLogin;
    TextView txtRegister, forgotPass;

    // Logged-in user's ID (from DB)
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        edtUsername = findViewById(R.id.edtUsernamel);
        edtPassword = findViewById(R.id.edtPasswordl);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegisterl);
        forgotPass = findViewById(R.id.txtForgotPasswordl);

        txtRegister.setOnClickListener(v ->
                startActivity(new Intent(Login_Screen.this, Register_Screen.class)));

        forgotPass.setOnClickListener(v ->
                startActivity(new Intent(Login_Screen.this, forgot_password.class)));

        btnLogin.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(Login_Screen.this, "Username and Password required", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(username, password);
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

                        if (response.body().isStatus()) {
                            int visitorId = Integer.parseInt(response.body().getUserId()); // ya phir response.body().getVisitorId()
                            saveFcmToken(visitorId); // ✅ Token save immediately
                        }


                        // Go to HomeActivity
                        startActivity(new Intent(Login_Screen.this, HomeActivity.class));
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

    private void saveFcmToken(int visitorId) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM token failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Log.d("FCM", "Token: " + token);

                    ApiService api = ApiClient.getClient().create(ApiService.class);
                    Call<ApiResponse> call = api.saveFcmToken(visitorId, token); // visitor_id

                    call.enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                                Log.d("FCM", "Token saved successfully for visitor: " + visitorId);
                            } else {
                                Log.e("FCM", "Token save failed! Response: " + response.body());
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {
                            Log.e("FCM", "Error saving token: " + t.getMessage());
                        }
                    });
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
