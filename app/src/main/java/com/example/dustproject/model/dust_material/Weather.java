package com.example.dustproject.model.dust_material;

import java.util.List;

public class Weather {
    private List<Dust> dust; // weather 안에 dust는 리스트 형태이다

    public List<Dust> getDust() {
        return dust;
    }

    public void setDust(List<Dust> dust) {
        this.dust = dust;
    }
}
