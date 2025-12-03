package com.example.visitormanagementsys;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.visitormanagementsys.Login.Login_Screen;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingActivity extends AppCompatActivity {

    TextView tvProfileName, tvProfileEmail, appversion, tvAboutContent, changepassword;
    private Button btnlogout;

    private static final int REQUEST_PICK_IMAGE = 1001;
    private ImageView imgProfile;
    private String savedUsername;

    private static final int PERMISSION_CODE = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        appversion = findViewById(R.id.appversion);
        btnlogout = findViewById(R.id.btnLogout);
        tvAboutContent = findViewById(R.id.tvAboutContent);
        changepassword = findViewById(R.id.changepassword);
        imgProfile = findViewById(R.id.imgProfile);

        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);

        savedUsername = prefs.getString("username", "N/A");
        String email = prefs.getString("email", "N/A");
        String savedImagePath = prefs.getString("profile_image", "");

        tvProfileName.setText(savedUsername);
        tvProfileEmail.setText(email);

        appversion.setText("App Version:- 1");

        // ---- Load Profile Image ----
        if (!savedImagePath.isEmpty()) {
            String baseUrl = "http://192.168.1.15:8085/vms/";   // CHANGE YOUR SERVER URL
            Glide.with(this)
                    .load(baseUrl + savedImagePath)
                    .placeholder(R.drawable.ic_person)
                    .into(imgProfile);
        } else {
            imgProfile.setImageResource(R.drawable.ic_person);
        }

        // ----- Image Click (select new) -----
        imgProfile.setOnClickListener(v -> {
            if (checkPermission()) {
                pickImage();
            }
        });

        // ----- Password Change -----
        changepassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });

        // ----- About -----
        tvAboutContent.setOnClickListener(v -> {
            startActivity(new Intent(this, activity_about.class));
        });

        // ----- Logout -----
        btnlogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(this, "Logout Successful", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(this, Login_Screen.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
    }

    // -------------------------------------------------------------
    //                PICK IMAGE FROM GALLERY
    // -------------------------------------------------------------
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    // -------------------------------------------------------------
    //                      PERMISSIONS
    // -------------------------------------------------------------
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_CODE);
                return false;
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_CODE);
                return false;
            }
        }
        return true;
    }

    // -------------------------------------------------------------
    //               RESULT OF IMAGE PICKING
    // -------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

            if (uri != null) {
                imgProfile.setImageURI(uri);  // Preview
                uploadImage(uri);
            }
        }
    }

    // -------------------------------------------------------------
    //                 UPLOAD IMAGE TO SERVER
    // -------------------------------------------------------------
    private void uploadImage(Uri uri) {
        String filePath = FileUtils.getPath(this, uri);

        if (filePath == null) {
            Toast.makeText(this, "Cannot read selected image", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(filePath);

        RequestBody reqFile = RequestBody.create(
                MediaType.parse(getContentResolver().getType(uri)),
                file
        );

        MultipartBody.Part body = MultipartBody.Part.createFormData(
                "image",
                file.getName(),
                reqFile
        );

        RequestBody usernamePart = RequestBody.create(
                savedUsername,
                MediaType.parse("text/plain")
        );

        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<UploadResponse> call = api.uploadProfileImage(body, usernamePart);

        call.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    UploadResponse res = response.body();

                    if (res.isStatus()) {
                        // Save new image path
                        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                        prefs.edit().putString("profile_image", res.getImage()).apply();

                        Toast.makeText(SettingActivity.this,
                                "Profile Image Updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SettingActivity.this,
                                res.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SettingActivity.this,
                            "Upload Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                Toast.makeText(SettingActivity.this,
                        "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
