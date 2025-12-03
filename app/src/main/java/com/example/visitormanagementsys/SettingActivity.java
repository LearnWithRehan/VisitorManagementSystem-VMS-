package com.example.visitormanagementsys;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

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
    Button btnLogout;
    ImageView imgProfile;

    private Uri imageUri;
    private File cameraFile;

    private static final String PREFS = "UserData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        appversion = findViewById(R.id.appversion);
        tvAboutContent = findViewById(R.id.tvAboutContent);
        changepassword = findViewById(R.id.changepassword);
        btnLogout = findViewById(R.id.btnLogout);
        imgProfile = findViewById(R.id.imgProfile);

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String username = prefs.getString("username", "N/A");
        String email = prefs.getString("email", "N/A");
        String profileImageUrl = prefs.getString("profile_image", "");

        tvProfileName.setText(username);
        tvProfileEmail.setText(email);
        appversion.setText("App Version:- 1");

        // ----------------------------------
        // 🔥 AUTO LOAD PROFILE IMAGE
        // ----------------------------------
        if (!profileImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(profileImageUrl)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .circleCrop()  // 🔥 Make it round
                    .into(imgProfile);
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_person)
                    .circleCrop()
                    .into(imgProfile);
        }


        imgProfile.setOnClickListener(v -> showImagePickerDialog());

        changepassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });

        tvAboutContent.setOnClickListener(v -> {
            startActivity(new Intent(this, activity_about.class));
        });

        btnLogout.setOnClickListener(v -> {

            // 1️⃣ Clear only username/email or session token
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("username");
            editor.remove("email");
            editor.apply();

            // 2️⃣ Profile image ko delete mat karo
            // imgProfile.setImageResource(R.drawable.ic_person); // optional

            // 3️⃣ Clear Glide memory
            Glide.get(SettingActivity.this).clearMemory();
            new Thread(() -> Glide.get(SettingActivity.this).clearDiskCache()).start();

            // 4️⃣ Move to login screen
            Intent i = new Intent(SettingActivity.this, Login_Screen.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });


    }

    // ===========================
    // IMAGE PICK DIALOG
    // ===========================
    private void showImagePickerDialog() {
        String[] options = {"Camera", "Gallery"};

        new AlertDialog.Builder(this)
                .setTitle("Select Profile Image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) openCamera();
                    else openGallery();
                }).show();
    }

    // ===========================
    // CAMERA
    // ===========================
    private void openCamera() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
            }
            return;
        }

        File dir = new File(getExternalFilesDir(null), "images");
        if (!dir.exists()) dir.mkdirs();

        cameraFile = new File(dir, "profile_camera.jpg");

        imageUri = FileProvider.getUriForFile(this,
                getPackageName() + ".provider", cameraFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        cameraLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

                if (result.getResultCode() == RESULT_OK) {
                    imgProfile.setImageURI(imageUri);
                    uploadImage(cameraFile);
                }
            });

    // ===========================
    // GALLERY
    // ===========================
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        galleryLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

                if (result.getResultCode() == RESULT_OK) {

                    Uri selectedUri = result.getData().getData();
                    imgProfile.setImageURI(selectedUri);

                    File file = FileUtils.getFileFromUri(this, selectedUri);
                    uploadImage(file);
                }
            });

    // ===========================
    // UPLOAD IMAGE TO SERVER
    // ===========================
    private void uploadImage(File file) {

        if (file == null) {
            Toast.makeText(this, "File not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part imgPart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String username = prefs.getString("username", "");

        RequestBody userPart = RequestBody.create(MediaType.parse("text/plain"), username);

        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<UploadResponse> call = api.uploadProfileImage(imgPart, userPart);

        call.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    UploadResponse res = response.body();

                    Toast.makeText(SettingActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();

                    // Save image URL for next time
                    prefs.edit().putString("profile_image", res.getImageUrl()).apply();
                }
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                Toast.makeText(SettingActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            openCamera();
        }
    }
}
