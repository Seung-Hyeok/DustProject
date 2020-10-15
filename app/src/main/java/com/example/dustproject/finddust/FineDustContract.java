package com.example.dustproject.finddust;

import com.example.dustproject.model.dust_material.FineDust;

public class FineDustContract {
    interface View{ // 유저의 행위가아닌 이외의 것 화면변화, 결과 등
        void showFineDustResult(FineDust fineDust);
        void showLoadError(String message);
        void loadingStart();
        void loadingEnd();
        void reload(double lat, double lng);

    }
    interface UserActionListener{ // 유저의 행위를 위한
        void loadFineDustData();
    }
}
