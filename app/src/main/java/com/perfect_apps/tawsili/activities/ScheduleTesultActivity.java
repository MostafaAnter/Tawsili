package com.perfect_apps.tawsili.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.perfect_apps.tawsili.R;
import com.perfect_apps.tawsili.scheduleing_task.PushLocalNotification;
import com.perfect_apps.tawsili.utils.SweetDialogHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScheduleTesultActivity extends LocalizationActivity implements View.OnClickListener{
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.button1)Button b1;
    @BindView(R.id.button2)Button b2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_tesult);
        ButterKnife.bind(this);
        setToolbar();
        changeFontOfText();

        b1.setOnClickListener(this);
        b2.setOnClickListener(this);

        if (getIntent().getStringExtra("flag") != null){
            createOrder();
        }

    }

    private void changeFontOfText() {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/normal.ttf");
        b1.setTypeface(font);
        b2.setTypeface(font);
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
        switch (v.getId()){
            case R.id.button1:
                // create order
                createOrder();
                break;
            case R.id.button2:
                finish();
                break;
        }
    }

    private void createOrder(){
        new SweetDialogHelper(this).showSuccessfulMessage("hhghhh", "done");
    }
}
