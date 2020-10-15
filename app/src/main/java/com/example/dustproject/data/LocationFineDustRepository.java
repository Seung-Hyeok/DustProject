package com.example.dustproject.data;

import com.example.dustproject.model.dust_material.FineDust;
import com.example.dustproject.util.FineDustUtil;

import retrofit2.Callback;

public class LocationFineDustRepository implements FineDustRepository {
    private FineDustUtil mFineDustUtil;
    private double mLatitude; // 위도
    private double mLongitude; // 경도

    public LocationFineDustRepository(){ // 기본 생성자
        mFineDustUtil = new FineDustUtil(); // FineDustUtil 초기화
    }
    public LocationFineDustRepository(double lat, double lng){ // 위도 경도 받는 생성자
        this(); // 기본 생성자 호출 하도록해줌
        this.mLatitude = lat;
        this.mLongitude = lng;
    }
    @Override
    public boolean isAvailable() {
        if(mLatitude != 0.0 && mLongitude != 0.0){ // 위도 경도가 0 이 아니면
            return true;
        }
        return false;
    }

    @Override
    public void getFindDustData(Callback<FineDust> callback) {
        mFineDustUtil.getApi().getFineDust(mLatitude,mLongitude).enqueue(callback);

    }
}
