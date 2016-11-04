package com.perfect_apps.tawsili.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.perfect_apps.tawsili.R;
import com.perfect_apps.tawsili.store.TawsiliPrefStore;
import com.perfect_apps.tawsili.utils.Constants;
import com.perfect_apps.tawsili.utils.CustomTypefaceSpan;
import com.perfect_apps.tawsili.utils.MapHelper;
import com.perfect_apps.tawsili.utils.MapStateManager;
import com.vipul.hp_hp.library.Layout_to_Image;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YourRideActivity extends LocalizationActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback{
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.nav_view)NavigationView navigationView;

    private GoogleMap mMap;
    private static final int GPS_ERRORDIALOG_REQUEST = 9001;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_ride);
        ButterKnife.bind(this);
        setToolbar();

        // for map
        if (servicesOK()) {
            initMap();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        changeFontOfNavigation();
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
                new TawsiliPrefStore(this).addPreference(Constants.PREFERENCE_DRIVER_LANGUAGE, 1);
                break;
            case "ar":
                new TawsiliPrefStore(this).addPreference(Constants.PREFERENCE_DRIVER_LANGUAGE, 0);
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
        setUpMarker(mMap, new LatLng(30.066649, 31.254493), new LatLng(30.067114, 31.253077));
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
}
