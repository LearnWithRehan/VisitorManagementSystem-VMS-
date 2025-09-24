package com.example.visitormanagementsys.Login;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Data data;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    // 👇 Helper method add kiya
    public String getUserId() {
        return data != null ? data.getId() : null;
    }

    public static class Data {
        @SerializedName("id")
        private String id;

        @SerializedName("username")
        private String username;

        @SerializedName("email")
        private String email;

        public String getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }
    }
}
