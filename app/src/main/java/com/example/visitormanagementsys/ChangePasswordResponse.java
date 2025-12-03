package com.example.visitormanagementsys;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordResponse {

    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    @SerializedName("otp_required")
    private boolean otpRequired;

    public boolean isStatus() { return status; }
    public String getMessage() { return message; }
    public boolean isOtpRequired() { return otpRequired; }
}
