package com.perfect_apps.tawsili.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.perfect_apps.tawsili.R;
import com.perfect_apps.tawsili.store.TawsiliPrefStore;
import com.perfect_apps.tawsili.utils.Constants;
import com.perfect_apps.tawsili.utils.CustomTypefaceSpan;
import com.perfect_apps.tawsili.utils.SweetDialogHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RateCategoriyActivity extends LocalizationActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener{

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.nav_view)NavigationView navigationView;

    @BindView(R.id.button1)Button button1;
    @BindView(R.id.button2)Button button2;

    @BindView(R.id.text1)TextView textView1;
    @BindView(R.id.text2)TextView textView2;
    @BindView(R.id.text3)TextView textView3;
    @BindView(R.id.text4)TextView textView4;
    @BindView(R.id.text5)TextView textView5;
    @BindView(R.id.text6)TextView textView6;
    @BindView(R.id.text7)TextView textView7;
    @BindView(R.id.text8)TextView textView8;
    @BindView(R.id.text9)TextView textView9;
    @BindView(R.id.text10)TextView textView10;


    private static String category = "1";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_categoriy);
        ButterKnife.bind(this);
        category = getIntent().getStringExtra(Constants.PREFERENCE_ORDER_TYPE);
        if (category != null){
            writeData(category, true);
        }else {
            writeData("1", true);
        }
        setToolbar();
        changeFontOfText();



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        changeFontOfNavigation();

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
    }

    private void writeData(String category, boolean isNow){
        float startCost = 0;
        float runningCost = 0;
        float minimumCost = 0;
        float waitingCost = 0;
        float pickFromAir = 0;
        switch (category){
            case "1":
                if (isNow){
                    startCost = Constants.startCostOfEconomyNow;
                    runningCost = Constants.runningCostOfEconomy;
                    minimumCost = Constants.minimumCostOfEconomyNow;
                    waitingCost = Constants.waitingCostOfEconomy;
                    pickFromAir = Constants.pickFromAirportCostOfEconomy;
                }else {
                    startCost = Constants.startCostOfEconomyLater;
                    runningCost = Constants.runningCostOfEconomy;
                    minimumCost = Constants.minimumCostOfEconomyLater;
                    waitingCost = Constants.waitingCostOfEconomy;
                    pickFromAir = Constants.pickFromAirportCostOfEconomy;
                }
                break;
            case "2":
                if (isNow){
                    startCost = Constants.startCostOfBusinessNow;
                    runningCost = Constants.runningCostOfBusiness;
                    minimumCost = Constants.minimumCostOfBusinessNow;
                    waitingCost = Constants.waitingCostOfBusiness;
                    pickFromAir = Constants.pickFromAirportCostOfBusiness;
                }else {
                    startCost = Constants.startCostOfBusinessLater;
                    runningCost = Constants.runningCostOfBusiness;
                    minimumCost = Constants.minimumCostOfBusinessLater;
                    waitingCost = Constants.waitingCostOfBusiness;
                    pickFromAir = Constants.pickFromAirportCostOfBusiness;
                }
                break;
            case "3":
                if (isNow){
                    startCost = Constants.startCostOfVIPNow;
                    runningCost = Constants.runningCostOfVIP;
                    minimumCost = Constants.minimumCostOfVIPNow;
                    waitingCost = Constants.waitingCostOfVIP;
                    pickFromAir = Constants.pickFromAirportCostOfVIP;
                }else {
                    startCost = Constants.startCostOfVIPLater;
                    runningCost = Constants.runningCostOfVIP;
                    minimumCost = Constants.minimumCostOfVIPLater;
                    waitingCost = Constants.waitingCostOfVIP;
                    pickFromAir = Constants.pickFromAirportCostOfVIP;
                }
                break;
            case "4":
                if (isNow){
                    startCost = Constants.startCostOfFamilitRegularNow;
                    runningCost = Constants.runningCostOfFamilitRegular;
                    minimumCost = Constants.minimumCostOfFamilitRegularNow;
                    waitingCost = Constants.waitingCostOfFamilitRegular;
                    pickFromAir = Constants.pickFromAirportCostOfFamilitRegular;
                }else {
                    startCost = Constants.startCostOfFamilitRegularLater;
                    runningCost = Constants.runningCostOfFamilitRegular;
                    minimumCost = Constants.minimumCostOfFamilitRegularLater;
                    waitingCost = Constants.waitingCostOfFamilitRegular;
                    pickFromAir = Constants.pickFromAirportCostOfFamilitRegular;
                }
                break;
            case "5":
                if (isNow){
                    startCost = Constants.startCostOfFamilitSpecialNow;
                    runningCost = Constants.runningCostOfFamilitSpecial;
                    minimumCost = Constants.minimumCostOfFamilitSpecialNow;
                    waitingCost = Constants.waitingCostOfFamilitSpecial;
                    pickFromAir = Constants.pickFromAirportCostOfFamilitSpecial;
                }else {
                    startCost = Constants.startCostOfFamilitSpecialLater;
                    runningCost = Constants.runningCostOfFamilitSpecial;
                    minimumCost = Constants.minimumCostOfFamilitSpecialLater;
                    waitingCost = Constants.waitingCostOfFamilitSpecial;
                    pickFromAir = Constants.pickFromAirportCostOfFamilitSpecial;
                }
                break;
        }

        textView2.setText(startCost + "");
        textView4.setText(runningCost + " per km");
        textView6.setText(waitingCost + " per hour");
        textView8.setText(minimumCost + "");
        textView10.setText(pickFromAir + "");

    }

    //change font of drawer
    private void changeFontOfNavigation(){
        Menu m = navigationView.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/normal.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    private void changeFontOfText(){
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/normal.ttf");
        Typeface fontBold = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");

        button1.setTypeface(fontBold);
        button2.setTypeface(fontBold);

        textView1.setTypeface(font);
        textView2.setTypeface(font);
        textView3.setTypeface(font);
        textView4.setTypeface(font);
        textView5.setTypeface(font);
        textView6.setTypeface(font);
        textView7.setTypeface(font);
        textView8.setTypeface(font);
        textView9.setTypeface(font);
        textView10.setTypeface(font);
    }



    private void setToolbar() {
        setSupportActionBar(toolbar);
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

        switch (category){
            case "1":
                tv.setText(getString(R.string.economy));
                break;
            case "2":
                tv.setText(getString(R.string.business));
                break;
            case "3":
                tv.setText(getString(R.string.vip));
                break;
            case "4":
                tv.setText(getString(R.string.regular_family));
                break;
            case "5":
                tv.setText(getString(R.string.special_family));
                break;

        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.my_rides_history) {
            startActivity(new Intent(this, MyRidesActivity.class));

        } else if (id == R.id.my_scheduled_rides) {
            startActivity(new Intent(this, MyScheduleOrdersActivity.class));

        } else if (id == R.id.invite_friends) {
            startActivity(new Intent(this, InviteFriendActivity.class));

        } else if (id == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }else if (id == R.id.english_speaking){
            showSingleChoiceListDrivereLangaugeAlertDialog();
        }else if (id == R.id.call_us){
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "00966920008819"));
            startActivity(callIntent);
        }else if (id == R.id.payment){
            new SweetDialogHelper(this).showTitleWithATextUnder("", "Cash method is available for now");
        }else if(id == R.id.book_a_ride){
            startActivity(new Intent(this, PickLocationActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // change driver language
    private String mCheckedDriverLanguageItem;

    public void showSingleChoiceListDrivereLangaugeAlertDialog() {
        final String[] list = new String[]{getString(R.string.language_arabic), getString(R.string.language_en)};
        int checkedItemIndex;

        switch (getDriverLanguage()) {
            case "2":
                checkedItemIndex = 1;
                break;
            default:
                checkedItemIndex = 0;

        }
        mCheckedDriverLanguageItem = list[checkedItemIndex];

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.language))
                .setSingleChoiceItems(list,
                        checkedItemIndex,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mCheckedDriverLanguageItem = list[which];
                                if (which == 0) {
                                    setDriverLanguage("ar");
                                    dialog.dismiss();
                                } else if (which == 1) {
                                    setDriverLanguage("en");
                                    dialog.dismiss();
                                }
                            }
                        })
                .show();
    }

    private void setDriverLanguage(String langauge){
        switch (langauge){
            case "en":
                new TawsiliPrefStore(this).addPreference(Constants.PREFERENCE_DRIVER_LANGUAGE, 2);
                break;
            case "ar":
                new TawsiliPrefStore(this).addPreference(Constants.PREFERENCE_DRIVER_LANGUAGE, 1);
                break;
        }

    }

    private String getDriverLanguage(){
        return String.valueOf(new TawsiliPrefStore(this)
                .getIntPreferenceValue(Constants.PREFERENCE_DRIVER_LANGUAGE));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button1:
                button1.setBackgroundResource(R.drawable.shape_button);
                button1.setTextColor(Color.BLACK);
                button2.setBackgroundResource(R.drawable.shape_button_dark);
                button2.setTextColor(Color.WHITE);
                writeData(category, true);
                break;
            case R.id.button2:
                button2.setBackgroundResource(R.drawable.shape_button);
                button2.setTextColor(Color.BLACK);
                button1.setBackgroundResource(R.drawable.shape_button_dark);
                button1.setTextColor(Color.WHITE);
                writeData(category, false);
                break;
        }
    }
}
