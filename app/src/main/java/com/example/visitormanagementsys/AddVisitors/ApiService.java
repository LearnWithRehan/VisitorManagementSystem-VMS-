package com.example.visitormanagementsys.AddVisitors;

import retrofit2.Call;
import retrofit2.http.Body;
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
}