package com.example.visitormanagementsys;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VisitorApiService {
    @GET("visitor_action.php")
    Call<Map<String, Object>> updateVisitorStatus(
            @Query("visitor_id") int visitorId,
            @Query("action") String action,
            @Query("employee") String employeeName
    );
}
