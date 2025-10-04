package com.example.visitormanagementsys.ActiveVisit;

import com.google.gson.annotations.SerializedName;

public class VisitorModel {
    @SerializedName("visitor_id")
    private String visitorId;

    @SerializedName("name")
    private String name;

    @SerializedName("mobile")
    private String mobile;

    @SerializedName("address")
    private String address;

    @SerializedName("company")
    private String company;

    @SerializedName("purpose")
    private String purpose;

    @SerializedName("department")
    private String department;

    @SerializedName("employee")
    private String employee;

    @SerializedName("Entry_Date")
    private String entryDate;

    // Getters
    public String getVisitorId() { return visitorId; }
    public String getName() { return name; }
    public String getMobile() { return mobile; }
    public String getAddress() { return address; }
    public String getCompany() { return company; }
    public String getPurpose() { return purpose; }
    public String getDepartment() { return department; }
    public String getEmployee() { return employee; }
    public String getEntryDate() { return entryDate; }
}
