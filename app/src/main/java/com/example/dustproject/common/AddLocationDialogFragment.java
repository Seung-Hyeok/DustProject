package com.example.dustproject.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.dustproject.R;

public class AddLocationDialogFragment extends DialogFragment {
    private EditText mCityEditText;

    private  OnClickListener mOkClickListener;

    public interface OnClickListener{
        void onOkClicked(String city);
    }
    public void setOnClickListener(OnClickListener listener){
        mOkClickListener = listener;
    }
    public static AddLocationDialogFragment newInstance(OnClickListener listener){
        Bundle args = new Bundle();
        AddLocationDialogFragment fragment = new AddLocationDialogFragment();
        fragment.setOnClickListener(listener);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_add_location, null,false);
        mCityEditText = view.findViewById(R.id.city_edit);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("위치 추가");
        builder.setView(view);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String city = mCityEditText.getText().toString();
                mOkClickListener.onOkClicked(city);
            }
        });
        builder.setNegativeButton("취소",null);

        return builder.create();
    }
}
