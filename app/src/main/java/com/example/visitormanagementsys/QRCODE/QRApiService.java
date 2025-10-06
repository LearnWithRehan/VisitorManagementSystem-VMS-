package com.example.visitormanagementsys.QRCODE;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface QRApiService {
    @FormUrlEncoded
    @POST("send_visitor_qr.php")
    Call<QRApiResponse> sendVisitorDetails(
            @Field("name") String name,
            @Field("email") String email,
            @Field("address") String address,
            @Field("purpose") String purpose,
            @Field("company") String company,
            @Field("image") String imageBase64
    );
}
