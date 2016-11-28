package com.perfect_apps.tawsili.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.perfect_apps.tawsili.R;
import com.perfect_apps.tawsili.activities.LoginActivity;
import com.perfect_apps.tawsili.activities.SignUpActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mostafa_anter on 10/21/16.
 */

public class SplashFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.button1) Button button1;
    @BindView(R.id.button2) Button button2;
    @BindView(R.id.button3) Button button3;
    @BindView(R.id.text1) TextView textView1;


    public SplashFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        changeFontOfText();

        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
    }

    private void changeFontOfText(){
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/normal.ttf");
        textView1.setTypeface(font);
        button1.setTypeface(font);
        button2.setTypeface(font);
        button3.setTypeface(font);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button1:
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+966920008819"));
                startActivity(callIntent);
                break;
            case R.id.button3:
                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.button2:
                getActivity().startActivity(new Intent(getActivity(), SignUpActivity.class));
                break;
        }
    }
}

