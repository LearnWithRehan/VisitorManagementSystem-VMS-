package com.example.visitormanagementsys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.visitormanagementsys.Login.Login_Screen;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText etUsername, etOldPassword, etNewPassword, etConfirmPassword;
    Button btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // ---------- Load Saved User Data ----------
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String savedUsername = prefs.getString("username", "");

        // ---------- Init Views ----------
        etUsername = findViewById(R.id.etUsername);
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        // ---------- Auto-Fill Username ----------
        etUsername.setText(savedUsername);

        // ---------- Username Should Not Be Editable ----------
        etUsername.setEnabled(false);
        etUsername.setFocusable(false);
        etUsername.setClickable(false);

        // ---------- Button Click ----------
        btnChangePassword.setOnClickListener(v -> changePassword());
    }


    private void changePassword() {

        String username = etUsername.getText().toString().trim();
        String oldPwd = etOldPassword.getText().toString().trim();
        String newPwd = etNewPassword.getText().toString().trim();
        String confirmPwd = etConfirmPassword.getText().toString().trim();

        // -------- Validation --------
        if (oldPwd.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPwd.equals(confirmPwd)) {
            Toast.makeText(this, "Confirm password does not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // -------- API CALL --------
        Call<ChangePasswordResponse> call = ApiClient.getClient()
                .create(ApiService.class)
                .changePassword(username, oldPwd, newPwd);

        call.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call,
                                   Response<ChangePasswordResponse> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(ChangePasswordActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                    return;
                }

                ChangePasswordResponse res = response.body();

                // -------- OLD PASSWORD INCORRECT --------
                if (!res.isStatus()) {
                    Toast.makeText(ChangePasswordActivity.this,
                            res.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    return; // 🔥 Login par nahi jana
                }

                // -------- PASSWORD UPDATED SUCCESSFULLY --------
                Toast.makeText(ChangePasswordActivity.this,
                        res.getMessage(),
                        Toast.LENGTH_LONG).show();

                Intent intent = new Intent(ChangePasswordActivity.this, Login_Screen.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                Toast.makeText(ChangePasswordActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
