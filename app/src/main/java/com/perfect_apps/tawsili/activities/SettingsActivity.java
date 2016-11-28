package com.perfect_apps.tawsili.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.perfect_apps.tawsili.BuildConfig;
import com.perfect_apps.tawsili.R;
import com.perfect_apps.tawsili.app.AppController;
import com.perfect_apps.tawsili.store.FavoritePlacesStore;
import com.perfect_apps.tawsili.store.SceduleStore;
import com.perfect_apps.tawsili.store.TawsiliPrefStore;
import com.perfect_apps.tawsili.utils.Constants;
import com.perfect_apps.tawsili.utils.CustomTypefaceSpan;
import com.perfect_apps.tawsili.utils.SweetDialogHelper;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends LocalizationActivity
        implements NavigationView.OnNavigationItemSelectedListener
, View.OnClickListener{

    private static final String TAG = "SettingsActivity";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.change_language) LinearLayout linearLayout1;
    @BindView(R.id.rateView)LinearLayout rateView;
    @BindView(R.id.change_password)LinearLayout changePasswordView;
    @BindView(R.id.feedbackAction)LinearLayout feedBackAction;
    @BindView(R.id.button1)Button button1;
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

    @BindView(R.id.nav_view)NavigationView navigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
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

        linearLayout1.setOnClickListener(this);
        button1.setOnClickListener(this);
        rateView.setOnClickListener(this);
        changePasswordView.setOnClickListener(this);
        feedBackAction.setOnClickListener(this);

        getUserData();
    }

    private void getUserData(){
        String url = BuildConfig.API_BASE_URL + "getuser.php?mail=" +
                new TawsiliPrefStore(this).getPreferenceValue(Constants.userEmail) + "&id="
                + new TawsiliPrefStore(this).getPreferenceValue(Constants.userId);
        // here should show dialog
        final SweetDialogHelper sdh = new SweetDialogHelper(this);
        sdh.showMaterialProgress(getString(R.string.loading));
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                sdh.dismissDialog();
                response = StringEscapeUtils.unescapeJava(response);

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String name = jsonObject.optString("name");
                        new TawsiliPrefStore(SettingsActivity.this).addPreference(Constants.userName, name);
                        bindData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                sdh.dismissDialog();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void bindData(){
        textView2.setText(new TawsiliPrefStore(this).getPreferenceValue(Constants.userName));
        textView4.setText(new TawsiliPrefStore(this).getPreferenceValue(Constants.userPhone));
        textView6.setText(new TawsiliPrefStore(this).getPreferenceValue(Constants.userEmail));
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
        textView1.setTypeface(fontBold);
        textView2.setTypeface(font);
        textView3.setTypeface(fontBold);
        textView4.setTypeface(font);
        textView5.setTypeface(fontBold);
        textView6.setTypeface(font);
        textView7.setTypeface(font);
        textView8.setTypeface(font);
        textView9.setTypeface(font);
        textView10.setTypeface(font);

        button1.setTypeface(font);

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

            startActivity(new Intent(SettingsActivity.this, PickLocationActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);

        } else {
            super.onBackPressed();
            startActivity(new Intent(SettingsActivity.this, PickLocationActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.my_rides_history) {
            startActivity(new Intent(SettingsActivity.this, MyRidesActivity.class));

        } else if (id == R.id.my_scheduled_rides) {
            startActivity(new Intent(this, MyScheduleOrdersActivity.class));

        } else if (id == R.id.invite_friends) {
            startActivity(new Intent(SettingsActivity.this, InviteFriendActivity.class));

        } else if (id == R.id.settings) {
            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
        }else if (id == R.id.english_speaking){
            showSingleChoiceListDrivereLangaugeAlertDialog();
        }else if (id == R.id.call_us){
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+966920008819"));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.change_language:
                showSingleChoiceListLangaugeAlertDialog();
                break;
            case R.id.button1:
                new TawsiliPrefStore(this).clearPreference();
                new FavoritePlacesStore(this).clearPreference();
                new SceduleStore(this).clearPreference();
                startActivity(new Intent(this, SplashActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);
                break;
            case R.id.rateView:
                startActivity(new Intent(this, RateActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);
                break;
            case R.id.change_password:
                startActivity(new Intent(this, ChangePasswordActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);
                break;
            case R.id.feedbackAction:
                showSingleChoiceListFeedbackAlertDialog();
                break;
        }
    }

    // change language
    private String mCheckedItem;

    public void showSingleChoiceListLangaugeAlertDialog() {
        final String[] list = new String[]{getString(R.string.language_arabic), getString(R.string.language_en)};
        int checkedItemIndex;

        switch (getLanguage()) {
            case "en":
                checkedItemIndex = 1;
                break;
            default:
                checkedItemIndex = 0;

        }
        mCheckedItem = list[checkedItemIndex];

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.language))
                .setSingleChoiceItems(list,
                        checkedItemIndex,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mCheckedItem = list[which];
                                if (which == 0) {
                                    setLanguage("ar");
                                    changeFirstTimeOpenAppState(4);
                                    dialog.dismiss();
                                } else if (which == 1) {
                                    setLanguage("en");
                                    changeFirstTimeOpenAppState(5);
                                    dialog.dismiss();
                                }
                            }
                        })
                .show();
    }

    private void changeFirstTimeOpenAppState(int language) {
        new TawsiliPrefStore(this).addPreference(Constants.PREFERENCE_FIRST_TIME_OPEN_APP_STATE, 1);
        new TawsiliPrefStore(this).addPreference(Constants.PREFERENCE_LANGUAGE, language);
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

    // for feedback
    private String mCheckedFeedItem;

    public void showSingleChoiceListFeedbackAlertDialog() {
        final String[] list = new String[]{getString(R.string.complaint), getString(R.string.suggestion),
                getString(R.string.other)};
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.feedback))
                .setSingleChoiceItems(list,
                        -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mCheckedFeedItem = list[which];
                                if (which == 0) {
                                    sendMessage("Complaint");
                                    dialog.dismiss();
                                } else if (which == 1) {
                                    sendMessage("Suggestion");
                                    dialog.dismiss();
                                }else if (which == 2) {
                                    sendMessage("Other");
                                    dialog.dismiss();
                                }
                            }
                        })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void sendMessage(String subject){
        Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.setData(Uri.parse("mailto:services@tawsili.com")); // or just "mailto:" for blank
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        startActivity(intent);
    }

}
