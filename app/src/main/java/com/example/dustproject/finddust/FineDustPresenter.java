package com.example.dustproject.finddust;

import com.example.dustproject.data.FineDustRepository;
import com.example.dustproject.model.dust_material.FineDust;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FineDustPresenter implements FineDustContract.UserActionListener { // FineDustContract안에 인터페이스인 UserActionListener 구현
    private final FineDustRepository mRepository; // interface
    private final FineDustContract.View mView; // interface

    public FineDustPresenter(FineDustRepository repository, FineDustContract.View view) {
        this.mRepository = repository;
        this.mView = view;
    }

    @Override
    public void loadFineDustData() {
        if(mRepository.isAvailable()){
            mView.loadingStart();
            mRepository.getFindDustData(new Callback<FineDust>() {
                @Override
                public void onResponse(Call<FineDust> call, Response<FineDust> response) {
                    mView.showFineDustResult(response.body());
                    mView.loadingEnd();
                }

                @Override
                public void onFailure(Call<FineDust> call, Throwable t) {
                    mView.showLoadError(t.getLocalizedMessage());
                    mView.loadingEnd();

                }
            });
        }

    }
}
