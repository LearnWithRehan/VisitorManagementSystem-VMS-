package com.example.visitormanagementsys;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @FormUrlEncoded
    @POST("change_password.php")
    Call<ChangePasswordResponse> changePassword(
            @Field("username") String username,
            @Field("old_password") String oldPassword,
            @Field("new_password") String newPassword
    );

    @FormUrlEncoded
    @POST("verify_otp.php")
    Call<ChangePasswordResponse> verifyOtp(
            @Field("username") String username,
            @Field("otp") String otp,
            @Field("new_password") String newPassword
    );


    // ApiService.java (add method)
    @Multipart
    @POST("upload_profile_image.php")
    Call<UploadResponse> uploadProfileImage(
            @Part MultipartBody.Part image,
            @Part("username") RequestBody username
    );

}
