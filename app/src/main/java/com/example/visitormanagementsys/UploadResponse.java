package com.example.visitormanagementsys;

public class UploadResponse {
    boolean status;
    String message;
    String image;
    String image_url;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getImage() {
        return image;
    }

    public String getImageUrl() {
        return image_url;
    }
}
