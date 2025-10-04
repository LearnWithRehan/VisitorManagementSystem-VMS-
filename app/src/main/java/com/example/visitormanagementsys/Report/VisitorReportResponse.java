package com.example.visitormanagementsys.Report;

import com.google.gson.annotations.SerializedName;

import java.util.List;
public class VisitorReportResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<ReportModel> data;

    public boolean isSuccess() {
        return success;
    }

    public List<ReportModel> getData() {
        return data;
    }
}