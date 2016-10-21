package com.perfect_apps.tawsili.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.perfect_apps.tawsili.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AskForEmailActivity extends LocalizationActivity implements View.OnClickListener {

    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.button1)
    Button button1;

    @BindView(R.id.editText1)
    EditText editText1;
    @BindView(R.id.editText2) EditText editText2;
    @BindView(R.id.editText3) EditText editText3;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_for_email);

        ButterKnife.bind(this);
        setToolbar();
        changeFontOfText();
        button1.setOnClickListener(this);

    }

    private void changeFontOfText(){
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/normal.ttf");
        button1.setTypeface(font);
        editText1.setTypeface(font);
        editText2.setTypeface(font);
        editText3.setTypeface(font);

    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*
        * hide title
        * */
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //toolbar.setNavigationIcon(R.drawable.ic_toolbar);
        toolbar.setTitle("");
        toolbar.setSubtitle("");

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        TextView tv = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tv.setTypeface(font);

    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(AskForEmailActivity.this, AskForVerificationCodeActivity.class));
    }
}
