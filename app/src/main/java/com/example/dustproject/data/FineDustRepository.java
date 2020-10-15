package com.example.dustproject.data;

import com.example.dustproject.model.dust_material.FineDust;

import retrofit2.Callback;

public interface FineDustRepository {
    boolean isAvailable(); // 정보를 가져올수 있느냐 없느냐 판단을 위해
    void getFindDustData(Callback<FineDust> callback);
}
