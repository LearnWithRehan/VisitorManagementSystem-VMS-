package com.example.visitormanagementsys.Register;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegisterApiService {

    @POST("register.php") // server endpoint
    Call<RegisterResponse> registerUser(@Body RegisterRequest request);

}
