package com.perfect_apps.tawsili.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.perfect_apps.tawsili.R;
import com.perfect_apps.tawsili.fragments.SplashFragment;
import com.perfect_apps.tawsili.store.TawsiliPrefStore;
import com.perfect_apps.tawsili.utils.Constants;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends LocalizationActivity {


    @BindView(R.id.imageView)
    ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set default language of activity
        setDefaultLanguage("en");

        if (checkFirstTimeOpenApp() == 0) {
            setLanguage(Locale.getDefault().getLanguage());
        } else {
            if (new TawsiliPrefStore(this).getIntPreferenceValue(Constants.PREFERENCE_LANGUAGE) == 4) {
                setLanguage("ar");
            } else if (new TawsiliPrefStore(this).getIntPreferenceValue(Constants.PREFERENCE_LANGUAGE) == 5) {
                setLanguage("en");
            } else {
                setLanguage(Locale.getDefault().getLanguage());
            }
        }

        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        loadSplashImage();

        animateImageView();
    }

    private void loadSplashImage() {
        Glide.with(this)
                .load(R.drawable.splash)
                .centerCrop()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(0.1f)
                .dontAnimate()
                .into(imageView);
    }

    private void animateImageView() {
        Animation fade0 = AnimationUtils.loadAnimation(this, R.anim.fade_in_enter);

        imageView.startAnimation(fade0);
        fade0.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // do some thing
                SplashFragment splashFragment = new SplashFragment();
                getSupportFragmentManager().
                        beginTransaction()
                        .add(R.id.container,
                                splashFragment).commit();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private int checkFirstTimeOpenApp() {
        return new TawsiliPrefStore(this).getIntPreferenceValue(Constants.PREFERENCE_FIRST_TIME_OPEN_APP_STATE);
    }
}
