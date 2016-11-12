package com.perfect_apps.tawsili.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.perfect_apps.tawsili.BuildConfig;
import com.perfect_apps.tawsili.R;
import com.perfect_apps.tawsili.app.AppController;
import com.perfect_apps.tawsili.store.TawsiliPrefStore;
import com.perfect_apps.tawsili.utils.Constants;
import com.perfect_apps.tawsili.utils.CustomTypefaceSpan;
import com.perfect_apps.tawsili.utils.MapHelper;
import com.perfect_apps.tawsili.utils.MapStateManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class YourRideActivity extends LocalizationActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, View.OnClickListener{
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.nav_view)NavigationView navigationView;
    @BindView(R.id.linearLayout1) LinearLayout linearLayout1;

    @BindView(R.id.secondCounter)TextView secondCounter;
    private static int counter = 60;
    @BindView(R.id.avatar)CircleImageView avatar;
    @BindView(R.id.driverName) TextView driverName;
    @BindView(R.id.car_name)TextView carName;
    @BindView(R.id.rateValue)TextView rateValue;
    @BindView(R.id.ratingBar)AppCompatRatingBar ratingBar;

    @BindView(R.id.button1)Button button1;
    @BindView(R.id.button2)Button button2;


    private GoogleMap mMap;
    private static final int GPS_ERRORDIALOG_REQUEST = 9001;

    private String driverMobiel = "";
    private String orderId;
    private String driverId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_ride);
        ButterKnife.bind(this);
        setToolbar();

        linearLayout1.setVisibility(View.GONE);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);

        // for map
        if (servicesOK()) {
            initMap();
        }
        orderId = getIntent().getStringExtra("orderID");
        driverId = getIntent().getStringExtra("driver_id");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        changeFontOfNavigation();

        getDriver(driverId);


    }

    private void animateView(LinearLayout frameLayout) {
        frameLayout.setVisibility(View.VISIBLE);
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, R.anim.push_up_enter_long);
        frameLayout.startAnimation(hyperspaceJumpAnimation);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button1:
                break;
            case R.id.button2:
                break;
        }
    }

    private class CounterTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            secondCounter.setText("60");
        }

        @Override
        protected Void doInBackground(String... params) {
            for (int i = counter; i > 0; i--) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            secondCounter.setText(getString(R.string.pay_penalty));
            secondCounter.setTextColor(Color.RED);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

            counter--;
            secondCounter.setText(counter + "");


        }
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
            startActivity(new Intent(YourRideActivity.this, MyRidesActivity.class));

        } else if (id == R.id.invite_friends) {
            startActivity(new Intent(YourRideActivity.this, InviteFriendActivity.class));

        } else if (id == R.id.settings) {
            startActivity(new Intent(YourRideActivity.this, SettingsActivity.class));
        }else if (id == R.id.english_speaking){
            showSingleChoiceListDrivereLangaugeAlertDialog();
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

    // setup map
    public boolean servicesOK() {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        }
        else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, GPS_ERRORDIALOG_REQUEST);
            dialog.show();
        }
        else {
            Toast.makeText(this, "Can't connect to Google Play services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void initMap() {
        if (mMap == null) {
            SupportMapFragment mapFrag =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mMap != null) {
            MapStateManager mgr = new MapStateManager(this);
            mgr.saveMapState(mMap);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MapStateManager mgr = new MapStateManager(this);
        CameraPosition position = mgr.getSavedCameraPosition();
        if (position != null && mMap != null) {
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
            mMap.moveCamera(update);
            mMap.setMapType(mgr.getSavedMapType());
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void setUpMarker(GoogleMap mMap, LatLng latLng, LatLng secLatLang) {

        Marker marker1 = MapHelper.setUpMarkerAndReturnMarker(mMap, latLng, R.drawable.car_marker);
        // for second location
        Marker marker2 = MapHelper.setUpMarkerAndReturnMarker(mMap, secLatLang, R.drawable.person_marker);

        //animate camera
        updateZoom(mMap, latLng, secLatLang);

        new FakeTask().execute(new MarkersModel(marker1, marker2));
    }

    /*
     * Zooms the map to show the area of interest based on the search radius
     */
    private void updateZoom(GoogleMap mMap, LatLng myLatLng, LatLng secLatLang) {
        LatLngBounds egypt = new LatLngBounds(
                myLatLng, secLatLang);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(egypt.southwest, 17));
    }
    // for animate marker to another marker
    private void animateAfterSeconds(Marker marker1, Marker marker2){
        MapHelper.animateMarkerTo(marker1, marker2.getPosition().latitude, marker2.getPosition().longitude);
    }

    private class FakeTask extends AsyncTask<MarkersModel, Void, MarkersModel>{


        @Override
        protected MarkersModel doInBackground(MarkersModel... params) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(MarkersModel markersModel) {
            super.onPostExecute(markersModel);
            animateAfterSeconds(markersModel.getMarker1(), markersModel.getMarker2());
        }
    }

    private class MarkersModel{
        private Marker marker1;
        private Marker marker2;

        public MarkersModel(Marker marker1, Marker marker2) {
            this.marker1 = marker1;
            this.marker2 = marker2;
        }

        public Marker getMarker1() {
            return marker1;
        }

        public Marker getMarker2() {
            return marker2;
        }
    }

    private void getOrder(String orderId){
        String url = BuildConfig.API_BASE_URL + "getorder.php?id=" + orderId;
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("checkOrder", response.toString());
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String status = jsonObject.optString("status");

                        if (status.equalsIgnoreCase("Canceled by Client")
                                || status.equalsIgnoreCase("Canceled by Admin")
                                || status.equalsIgnoreCase("Client Didn't Attend")){
                            final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(YourRideActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Order Canceled!")
                                    .setContentText("this order is missed if you want, create new one")
                                    .setConfirmText("Ok, i know")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                        }
                                    });
                            sweetAlertDialog.show();
                            new AsyncTask<Void, Void, Void>(){

                                @Override
                                protected Void doInBackground(Void... params) {
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void aVoid) {
                                    super.onPostExecute(aVoid);
                                    sweetAlertDialog.dismissWithAnimation();
                                    Intent intent = new Intent(YourRideActivity.this,
                                            PickLocationActivity.class);
                                    startActivity(intent);
                                }
                            }.execute();

                        }else if (status.equalsIgnoreCase("Done")){
                            Intent intent = new Intent(YourRideActivity.this,
                                    YourRideTwoActivity.class);
                            startActivity(intent);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("checkOrder", "Error: " + error.getMessage());
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void getDriverLocation(String driverId){
        String url = BuildConfig.API_BASE_URL + "driverlocation.php?id=" + driverId;
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("checkOrder", response.toString());
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String current_location_lat = jsonObject.optString("current_location_lat");
                        String current_location_lng = jsonObject.optString("current_location_lng");

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("checkOrder", "Error: " + error.getMessage());
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void getDriver(String driverId){
        String url = BuildConfig.API_BASE_URL + "getdriver.php?id=" + driverId;
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("checkOrder", response.toString());
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject driverObject = jsonArray.getJSONObject(i);
                        String language_id = driverObject.optString("language_id");
                        String language = driverObject.optString("language");
                        String driver_id = driverObject.optString("driver_id");
                        String ssno = driverObject.optString("ssno");
                        String name = driverObject.optString("name");
                        String mobile = driverObject.optString("mobile");
                        String email = driverObject.optString("email");
                        String birth_date = driverObject.optString("birth_date");
                        String nationality = driverObject.optString("nationality");
                        String hire_date = driverObject.optString("hire_date");
                        String driving_license_exp = driverObject.optString("driving_license_exp");
                        String status = driverObject.optString("status");
                        String enable = driverObject.optString("enable");
                        String img_name = driverObject.optString("img_name");
                        String current_location_lat = driverObject.optString("current_location_lat");
                        String current_location_lng = driverObject.optString("current_location_lng");
                        String un = driverObject.optString("un");
                        String car_id = driverObject.optString("car_id");
                        String car_from = driverObject.optString("car_from");
                        String car_by_un = driverObject.optString("car_by_un");
                        String status_by = driverObject.optString("status_by");
                        String car_type = driverObject.optString("car_type");
                        String model = driverObject.optString("model");
                        String category = driverObject.optString("category");
                        String category2 = driverObject.optString("category2");
                        String payment_machine = driverObject.optString("payment_machine");
                        String capacity = driverObject.optString("capacity");
                        String join_date = driverObject.optString("join_date");
                        String udid = driverObject.optString("udid");
                        String license_plate = driverObject.optString("license_plate");
                        String rate = driverObject.optString("rate");

                        driverMobiel = mobile;
                        driverName.setText(name);
                        carName.setText(car_type);
                        rateValue.setText(rate);
                        ratingBar.setRating(Float.valueOf(rate));
                        // populate mainImage
                        Glide.with(YourRideActivity.this)
                                .load("http://tawsely.com/img/drivers/" + img_name)
                                .placeholder(R.color.gray_btn_bg_color)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .crossFade()
                                .dontAnimate()
                                .thumbnail(0.2f)
                                .into(avatar);

                        animateView(linearLayout1);

                        new CounterTask().execute();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("checkOrder", "Error: " + error.getMessage());
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void updateUserPalance(String balance){
        String url = BuildConfig.API_BASE_URL + "updateuserbalance.php?balance=" +
                balance + "&id=" +
                new TawsiliPrefStore(this).getPreferenceValue(Constants.userId);
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("checkOrder", response.toString());

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("checkOrder", "Error: " + error.getMessage());
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void updateOrderStatus(){

        String url = BuildConfig.API_BASE_URL + "updateorderstatus.php?order="
                + orderId + "&status=4";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("checkOrder", response.toString());

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("checkOrder", "Error: " + error.getMessage());
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }
}
