package com.example.dustproject.util;

import com.example.dustproject.model.dust_material.FineDust;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface FineDustApi {
    String BASE_URL = "http://api.weatherplanet.co.kr/";

    @Headers("appkey:6b200e091d1a4d7e83fb9b4732809b33")
    @GET("weather/dust?version=1")
    Call<FineDust> getFineDust(@Query("lat")double latitude,
                               @Query("lon")double longitude); // 응답 받는형태 FineDust 형태,  메서드이릅 적당히 만들어줌
}
