package com.example.visitormanagementsys.ActiveVisit;

import retrofit2.Call;
import retrofit2.http.GET;

public interface VisitorApiService {
    @GET("activevisitors.php") // path relative to your base URL
    Call<VisitorApiResponse> getActiveVisitors();
}
