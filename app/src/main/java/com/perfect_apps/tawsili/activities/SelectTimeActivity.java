package com.perfect_apps.tawsili.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.perfect_apps.tawsili.BuildConfig;
import com.perfect_apps.tawsili.R;
import com.perfect_apps.tawsili.app.AppController;
import com.perfect_apps.tawsili.fragments.DatePickerFragment;
import com.perfect_apps.tawsili.fragments.TimePickerFragment;
import com.perfect_apps.tawsili.models.PickDateEvent;
import com.perfect_apps.tawsili.models.PickTimeEvent;
import com.perfect_apps.tawsili.store.TawsiliPrefStore;
import com.perfect_apps.tawsili.utils.Constants;
import com.perfect_apps.tawsili.utils.CustomRequest;
import com.perfect_apps.tawsili.utils.CustomTypefaceSpan;
import com.perfect_apps.tawsili.utils.SweetDialogHelper;
import com.perfect_apps.tawsili.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class SelectTimeActivity extends LocalizationActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AdapterView.OnItemSelectedListener , View.OnClickListener{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.choose_iteration)
    Spinner spinner;
    @BindView(R.id.pick_date)Button button1;
    @BindView(R.id.pick_time)Button button2;
    @BindView(R.id.button1)Button button3;

    // date and time selected
    private String selectedDate, selectedTime,
    createdTime, timeOfSchedule;

    // parameter to create schedule
    private String fromDetails = "",
            toDetails = "",
            carType ,
            typeOfScheduleValue = "1",
            promoCode = "0",
            discount = "0";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_time);
        ButterKnife.bind(this);
        setToolbar();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        changeFontOfNavigation();

        // get extras
        if (getIntent().getStringExtra("promoCode") != null)
            promoCode = getIntent().getStringExtra("promoCode");
        if (getIntent().getStringExtra("discount") != null)
            discount = getIntent().getStringExtra("discount");

        setSpinner();
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void setSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.options_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
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

    }

    //change font of drawer
    private void changeFontOfNavigation() {
        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
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
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
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
            startActivity(new Intent(SelectTimeActivity.this, MyRidesActivity.class));

        } else if (id == R.id.invite_friends) {
            startActivity(new Intent(SelectTimeActivity.this, InviteFriendActivity.class));

        } else if (id == R.id.settings) {
            startActivity(new Intent(SelectTimeActivity.this, SettingsActivity.class));
        }else if (id == R.id.english_speaking){
            showSingleChoiceListDrivereLangaugeAlertDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                typeOfScheduleValue = "1";
                break;
            case 1:
                typeOfScheduleValue = "2";
                break;
            case 2:
                typeOfScheduleValue = "3";
                break;
            case 3:
                typeOfScheduleValue = "4";
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pick_date:
                DialogFragment newFragment1 = new DatePickerFragment();
                newFragment1.show(getSupportFragmentManager(), "datePicker");
                break;
            case R.id.pick_time:
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
                break;
            case R.id.button1:
                // createschedule api
                // show message schedule save
                if (checkBeforeCreateSchedule()){
                   // createSchedule();
                }
                break;
        }
    }

    @Subscribe
    public void onMessageEvent(PickDateEvent event) {
        String s = event.getYear() + "/" + event.getMonth() + "/" + event.getDay();
        button1.setText(s);
        selectedDate = event.getYear() + "-" + event.getMonth() + "-" + event.getDay();

    }

    @Subscribe
    public void onMessageEvent(PickTimeEvent event) {
        String s = event.getHourOfDay() + ":" + event.getMinute();
        button2.setText(s);
        selectedTime = event.getHourOfDay() + ":" + event.getMinute() + ":" + "00";

        int hours = event.getHourOfDay();
        int minuts = event.getMinute();

        if (minuts > 30){
            minuts -= 30;
        }else {
            hours -= 1 ;
            minuts += 30;
        }

        createdTime = hours + ":" + minuts + ":" + "00";

    }

    // change driver language
    private String mCheckedDriverLanguageItem;

    public void showSingleChoiceListDrivereLangaugeAlertDialog() {
        final String[] list = new String[]{getString(R.string.language_arabic), getString(R.string.language_en)};
        int checkedItemIndex;

        switch (getDriverLanguage()) {
            case "1":
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

    private void createSchedule(){
        String url = BuildConfig.API_BASE_URL + "createorder.php";
        // here should show dialog
        final SweetDialogHelper sweetDialogHelper = new SweetDialogHelper(this);
        sweetDialogHelper.showMaterialProgress(getString(R.string.loading));
        JSONObject params = new JSONObject();
        try {
            params.put("client", new TawsiliPrefStore(this)
                    .getPreferenceValue(Constants.userId));
            params.put("created", createdTime);
            params.put("time", timeOfSchedule);
            params.put("fromlat", new TawsiliPrefStore(this)
                    .getPreferenceValue(Constants.userLastLocationLat));
            params.put("fromlng", new TawsiliPrefStore(this)
                    .getPreferenceValue(Constants.userLastLocationLng));
            params.put("tolat", new TawsiliPrefStore(this)
                    .getPreferenceValue(Constants.userLastDropOffLocationLat));
            params.put("tolng", new TawsiliPrefStore(this)
                    .getPreferenceValue(Constants.userLastDropOffLocationLng));
            params.put("fromdetails", fromDetails);
            params.put("todetails", toDetails);


            params.put("type", typeOfScheduleValue);
            params.put("category", carType);

            params.put("promocode", promoCode);
            params.put("discount", discount);


        } catch (JSONException e) {
            e.printStackTrace();
            sweetDialogHelper.dismissDialog();
        }

        CustomRequest strReq = new CustomRequest(Request.Method.POST,
                url, params, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response1) {

                String response = response1.toString();
                Log.d("create order", response.toString());

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String result = jsonObject.optString("error");
                        String orderID = jsonObject.optString("id");
                        if (!result.trim().isEmpty()) {
                            sweetDialogHelper.dismissDialog();
                            new SweetAlertDialog(SelectTimeActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText(getString(R.string.error))
                                    .setContentText(result + " " + getString(R.string.try_agin))
                                    .setConfirmText(getString(R.string.yes_try_again))
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                           // recall function that create schedule
                                        }
                                    })
                                    .show();
                            break;
                        } else {
                            // go to home activity
                            sweetDialogHelper.dismissDialog();
                            sweetDialogHelper.showSuccessfulMessage("Done!", "Your schedule saved success :)");
                            new AsyncTask<Void, Void, Void>(){

                                @Override
                                protected Void doInBackground(Void... params) {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void aVoid) {
                                    super.onPostExecute(aVoid);
                                    startActivity(new Intent(SelectTimeActivity.this, PickLocationActivity.class)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);
                                }
                            }.execute();



                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    sweetDialogHelper.dismissDialog();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("error create order", "Error: " + error.toString());
                sweetDialogHelper.dismissDialog();
                sweetDialogHelper.showErrorMessage(getString(R.string.error),
                        getString(R.string.try_agin));
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);

    }

    private boolean checkBeforeCreateSchedule(){

        if (selectedTime != null && selectedDate != null){

            createdTime = selectedDate + " " + createdTime;
            timeOfSchedule = selectedDate + " " + selectedTime;

            return checkForOneHour();


        }else {
            new SweetDialogHelper(this).showErrorMessage("Select Date and time",
                    getString(R.string.try_agin));
            return false;
        }
    }

    private boolean checkForOneHour(){
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {

            Date date1 = simpleDateFormat.parse(timeOfSchedule);
            Date date2 = simpleDateFormat.parse(Utils.returnTime());

            return printDifference(date1, date2);

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean printDifference(Date startDate, Date endDate){

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        if (elapsedHours >= 1){
            return true;
        }else {
            new SweetDialogHelper(this).showErrorMessage("schedule at least one hour later",
                    getString(R.string.try_agin));
            return false;
        }

    }
}
