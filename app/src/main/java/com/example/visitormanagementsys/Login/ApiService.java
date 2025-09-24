package com.example.visitormanagementsys.Login;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    @FormUrlEncoded
    @POST("login.php") // aapke server ka endpoint
    Call<LoginResponse> loginUser(
            @Field("username") String username,
            @Field("password") String password
    );

//    @FormUrlEncoded
//    @POST("save_token.php")
//    Call<ApiResponse> saveFcmToken(
//            @Field("user_id") int userId,
//            @Field("fcm_token") String fcmToken
//    );
//

    @FormUrlEncoded
    @POST("save_token.php")
    Call<ApiResponse> saveFcmToken(
            @Field("visitor_id") int visitorId, // ✅ Same as PHP
            @Field("fcm_token") String fcmToken
    );

}