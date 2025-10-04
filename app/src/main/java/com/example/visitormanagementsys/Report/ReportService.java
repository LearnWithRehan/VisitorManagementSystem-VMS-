package com.example.visitormanagementsys.Report;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ReportService {
    @GET("get_visitors_report.php")
    Call<VisitorReportResponse> getVisitorsReport(
            @Query("from_date") String fromDate,
            @Query("to_date") String toDate
    );
}
