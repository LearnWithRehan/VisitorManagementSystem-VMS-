package com.example.visitormanagementsys.ActiveVisit;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface VisitorApiService {
    @GET("activevisitors.php") // path relative to your base URL
    Call<VisitorApiResponse> getActiveVisitors();

    @FormUrlEncoded
    @POST("update_flag.php")
    Call<ApiResponse> markVisitorInactive(@Field("visitor_id") int visitorId);

}
