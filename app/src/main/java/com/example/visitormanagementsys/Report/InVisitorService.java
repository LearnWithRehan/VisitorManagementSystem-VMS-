package com.example.visitormanagementsys.Report;

import retrofit2.Call;
import retrofit2.http.GET;

public interface InVisitorService {
    @GET("activeInPlantreport.php")  // Replace with actual PHP filename
    Call<InVisitorResponse> getInVisitors();
}
