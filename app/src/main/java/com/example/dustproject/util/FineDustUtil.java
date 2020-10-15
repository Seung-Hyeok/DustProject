package com.example.dustproject.util;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FineDustUtil {
    private FineDustApi mGetApi;

    public FineDustUtil(){
        Retrofit mRetrofit = new Retrofit.Builder() // retrofit 생성
                .baseUrl(FineDustApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // json 쉽게 파싱하기위함
                .build();
        mGetApi = mRetrofit.create(FineDustApi.class);
    }
    public FineDustApi getApi(){
        return mGetApi;
    }
}
