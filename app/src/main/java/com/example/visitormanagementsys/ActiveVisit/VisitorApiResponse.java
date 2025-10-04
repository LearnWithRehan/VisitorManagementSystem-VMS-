package com.example.visitormanagementsys.ActiveVisit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VisitorApiResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<VisitorModel> data;

    public boolean isSuccess() { return success; }
    public List<VisitorModel> getData() { return data; }
}
