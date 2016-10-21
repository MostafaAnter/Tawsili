package com.perfect_apps.tawsili.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.perfect_apps.tawsili.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends LocalizationActivity implements View.OnClickListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.text1) TextView textView1;
    @BindView(R.id.text2) TextView textView2;

    @BindView(R.id.button1)
    Button button1;

    @BindView(R.id.editText1) EditText editText1;
    @BindView(R.id.editText2) EditText editText2;
    @BindView(R.id.editText3) EditText editText3;
    @BindView(R.id.editText4) EditText editText4;
    @BindView(R.id.editText5) EditText editText5;
    @BindView(R.id.editText6) EditText editText6;

    @BindView(R.id.checkbox1)
    CheckBox checkBox1;

    @BindView(R.id.signUpUsingFacebook)
    LinearLayout linearLayout1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        setToolbar();
        changeFontOfText();
        linearLayout1.setOnClickListener(this);
        button1.setOnClickListener(this);
    }

    private void changeFontOfText(){
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/normal.ttf");
        textView1.setTypeface(font);
        textView2.setTypeface(font);

        button1.setTypeface(font);
        editText1.setTypeface(font);
        editText2.setTypeface(font);
        editText3.setTypeface(font);
        editText4.setTypeface(font);
        editText5.setTypeface(font);
        editText6.setTypeface(font);
        checkBox1.setTypeface(font);

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

    public void onCheckboxClicked(View view) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button1:
                startActivity(new Intent(SignUpActivity.this, AskForVerificationCodeActivity.class));
                break;
            case R.id.signUpUsingFacebook:
                startActivity(new Intent(SignUpActivity.this, AskForEmailActivity.class));
                break;
        }
    }
}
