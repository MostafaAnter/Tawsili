package com.perfect_apps.tawsili.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.perfect_apps.tawsili.fragments.RatingDialogFragment;
import com.perfect_apps.tawsili.models.PickDateEvent;
import com.perfect_apps.tawsili.models.RateEvent;
import com.perfect_apps.tawsili.store.TawsiliPrefStore;
import com.perfect_apps.tawsili.utils.Constants;
import com.perfect_apps.tawsili.utils.CustomTypefaceSpan;
import com.perfect_apps.tawsili.utils.MapHelper;
import com.perfect_apps.tawsili.utils.MapStateManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class YourRideThirdActivity extends LocalizationActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.pickUpInfo)
    TextView pickUpInfo;
    @BindView(R.id.dropOff)
    TextView dropOffInfo;
    @BindView(R.id.distance)
    TextView distance;
    @BindView(R.id.trip_fair)
    TextView tripFare;
    @BindView(R.id.discount)
    TextView discount;
    @BindView(R.id.paid_from_balance)
    TextView paidFromPalance;
    @BindView(R.id.totalFare)
    TextView totalFair;
    @BindView(R.id.paid_with)
    TextView paidWith;

    @BindView(R.id.ratingBar)
    AppCompatRatingBar ratingBar;
    @BindView(R.id.ratingView)
    LinearLayout ratingView;

    private List<Marker> markers;
    private Marker marker1, marker2;


    private GoogleMap mMap;
    private static final int GPS_ERRORDIALOG_REQUEST = 9001;

    private String orderId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_ride_third);
        ButterKnife.bind(this);
        setToolbar();

        // for map
        if (servicesOK()) {
            initMap();
        }

        markers = new ArrayList<>();

        orderId = getIntent().getStringExtra("orderId");

        ratingView.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        changeFontOfNavigation();

        getOrder(orderId);
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
            startActivity(new Intent(YourRideThirdActivity.this, PickLocationActivity.class)
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
            startActivity(new Intent(this, MyRidesActivity.class));

        }else if (id == R.id.my_scheduled_rides) {
            startActivity(new Intent(this, MyScheduleOrdersActivity.class));

        }  else if (id == R.id.invite_friends) {
            startActivity(new Intent(this, InviteFriendActivity.class));

        } else if (id == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.english_speaking) {
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

    private void setDriverLanguage(String langauge) {
        switch (langauge) {
            case "en":
                new TawsiliPrefStore(this).addPreference(Constants.PREFERENCE_DRIVER_LANGUAGE, 2);
                break;
            case "ar":
                new TawsiliPrefStore(this).addPreference(Constants.PREFERENCE_DRIVER_LANGUAGE, 1);
                break;
        }

    }

    private String getDriverLanguage() {
        return String.valueOf(new TawsiliPrefStore(this)
                .getIntPreferenceValue(Constants.PREFERENCE_DRIVER_LANGUAGE));
    }

    // setup map
    public boolean servicesOK() {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, GPS_ERRORDIALOG_REQUEST);
            dialog.show();
        } else {
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

    @Subscribe
    public void onMessageEvent(RateEvent event) {
        startActivity(new Intent(YourRideThirdActivity.this, PickLocationActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
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

    private void getOrder(final String orderId) {
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
                        String from_location_lat = jsonObject.optString("from_location_lat");
                        double from_lat = Double.valueOf(from_location_lat);
                        String from_location_lng = jsonObject.optString("from_location_lng");
                        double from_lng = Double.valueOf(from_location_lng);
                        String to_location_lat = jsonObject.optString("to_location_lat");
                        double to_lat = Double.valueOf(to_location_lat);
                        String to_location_lng = jsonObject.optString("to_location_lng");
                        double to_lng = Double.valueOf(to_location_lng);

                        String paid_amount = jsonObject.optString("paid_amount");
                        double paidAmount = Double.valueOf(paid_amount);
                        String real_fee = jsonObject.optString("real_fee");
                        double realFee = Double.valueOf(real_fee);
                        String payment_type = jsonObject.optString("payment_type");
                        String driver_id = jsonObject.optString("driver_id");
                        if (!driver_id.equalsIgnoreCase("null")&&
                                !driver_id.isEmpty()){
                            getDriver(driver_id);
                        }

                        tripFare.setText(paid_amount + " SR");
                        totalFair.setText(real_fee + " SR");

                        paidFromPalance.setText("0 SR");
                        discount.setText("0 SR");
                        if (realFee - paidAmount < 0){
                            double discou =1 - (paidAmount - realFee) / paidAmount;
                            discount.setText(discou + " SR");
                            paidFromPalance.setText("0 SR");
                        }else if (realFee - paidAmount > 0){
                            discount.setText("0 SR");
                            double discou = realFee - paidAmount;
                            paidFromPalance.setText(discou + " SR");
                        }

                        String kms = jsonObject.optString("kms");
                        distance.setText(kms + " KM");
                        paidWith.setText(payment_type);

                        try {
                            if (from_lat != 0 && from_lng != 0) {
                                getAddressInfo(new LatLng(from_lat, from_lng), pickUpInfo);

                                if (marker1 != null){
                                    marker1.remove();
                                    markers.remove(marker1);
                                    marker1 = null;
                                }
                                setUpMarker(mMap, new LatLng(from_lat,
                                        from_lng));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            if (to_lat != 0 && to_lng != 0) {
                                getAddressInfo(new LatLng(to_lat, to_lng), dropOffInfo);
                                marker2 = MapHelper.setUpMarkerAndReturnMarker(mMap,
                                        new LatLng(to_lat,
                                                to_lng), R.drawable.flag_marker);
                                markers.add(marker2);
                                centerAllMarker();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
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

    private void setUpMarker(GoogleMap mMap, LatLng latLng) {

        marker1 = MapHelper.setUpMarkerAndReturnMarker(mMap, latLng, R.drawable.person_marker);
        // add to marker list
        markers.add(marker1);

        //animate camera
        updateZoom(mMap, latLng);

        centerAllMarker();
    }

    private void centerAllMarker() {
        if (markers.size() > 1) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : markers) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();
            mMap.setPadding(200, 150, 200, 500);
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
            mMap.animateCamera(cu);
        }else if (markers.size() == 1){
            // Zoom to the given bounds
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markers.get(0).getPosition(), 14));
            // set draggable false done
            mMap.getUiSettings().setScrollGesturesEnabled(false);
        }


    }

    private void updateZoom(GoogleMap mMap, LatLng myLatLng) {
        // Zoom to the given bounds
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 14));
        // set draggable false done
        mMap.getUiSettings().setScrollGesturesEnabled(false);
    }

    private void getAddressInfo(LatLng latLng, TextView tv) throws IOException {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

        StringBuilder sb = new StringBuilder();

        if (address != null)
            sb.append(address);
        if (city != null)
            sb.append(", " + city);
        if (state != null)
            sb.append(", " + state);
        if (country != null)
            sb.append(", " + country);
        if (knownName != null)
            sb.append(", " + knownName);

        tv.setText(sb);

        Log.e("address info", sb.toString());

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ratingView:
                openRatingDialog();
                break;
        }
    }

    private static int mStackLevel = 0;

    private void openRatingDialog() {
        mStackLevel++;
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        Fragment prev1 = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev1 != null) {
            ft1.remove(prev1);
        }
        ft1.addToBackStack(null);

        // Create and show the dialog.
        RatingDialogFragment newFragment1 = RatingDialogFragment.newInstance(mStackLevel);
        Bundle bundle1 = new Bundle();
        bundle1.putString("orderID", orderId);
        newFragment1.setArguments(bundle1);
        newFragment1.show(ft1, "dialog");
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

                        ratingBar.setRating(Float.valueOf(rate));

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
}
