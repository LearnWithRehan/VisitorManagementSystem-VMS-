package com.example.visitormanagementsys.Report;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InVisitorResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<InVisitor> data;

    public boolean isSuccess() { return success; }
    public List<InVisitor> getData() { return data; }
}