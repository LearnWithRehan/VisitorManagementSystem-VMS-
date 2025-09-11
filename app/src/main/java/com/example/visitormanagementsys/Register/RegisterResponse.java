package com.example.visitormanagementsys.Register;


import com.google.gson.annotations.SerializedName;

public class RegisterResponse {

    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}