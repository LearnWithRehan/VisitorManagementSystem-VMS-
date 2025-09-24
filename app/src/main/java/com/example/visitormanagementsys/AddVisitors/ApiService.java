package com.example.visitormanagementsys.AddVisitors;

import com.example.visitormanagementsys.Login.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("addVisitor.php")  // PHP API ka naam
    Call<ResponseModel> addVisitor(@Body AddVisitorModel visitor);

    @GET("getDepartments.php")
    Call<DepartmentResponse> getDepartments();

    @POST("getEmployees.php")
    Call<EmployeeResponse> getEmployees(@Body DepartmentRequest request);

    @FormUrlEncoded
    @POST("save_fcm_token.php")
    Call<ApiResponse> saveFcmToken(
            @Field("user_id") int userId,
            @Field("fcm_token") String fcmToken
    );

}