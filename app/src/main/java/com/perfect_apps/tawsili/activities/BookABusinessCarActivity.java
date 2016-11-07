package com.perfect_apps.tawsili.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookABusinessCarActivity extends LocalizationActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, View.OnClickListener {

    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.linearLayout1)
    LinearLayout linearLayout1;
    @BindView(R.id.button1)
    Button button1;

    @BindView(R.id.current_location_button)
    LinearLayout pickCurrentLocation;
    @BindView(R.id.drop_off_location_button)
    LinearLayout pickDropOffLocation;
    @BindView(R.id.drop_off_line)
    View lineSeperator;

    @BindView(R.id.add_promo_code)
    TextView addPromoCode;

    @BindView(R.id.current_location_text)
    TextView curentLocationText;
    @BindView(R.id.drop_off_location_text)
    TextView dropOffLocationText;

    private GoogleMap mMap;
    private static final int GPS_ERRORDIALOG_REQUEST = 9001;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_abusiness_car);
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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        changeFontOfNavigation();
        animateView(linearLayout1);

        if (!getIntent().getBooleanExtra("now", false)) {
            button1.setText(getString(R.string.select_time));
        }
        button1.setOnClickListener(this);
        pickCurrentLocation.setOnClickListener(this);
        pickDropOffLocation.setOnClickListener(this);
        addPromoCode.setOnClickListener(this);
    }

    private void animateView(LinearLayout frameLayout) {
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, R.anim.push_up_enter_long);
        frameLayout.startAnimation(hyperspaceJumpAnimation);

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
            startActivity(new Intent(BookABusinessCarActivity.this, MyRidesActivity.class));

        } else if (id == R.id.invite_friends) {
            startActivity(new Intent(BookABusinessCarActivity.this, InviteFriendActivity.class));

        } else if (id == R.id.settings) {
            startActivity(new Intent(BookABusinessCarActivity.this, SettingsActivity.class));
        } else if (id == R.id.english_speaking) {
            showSingleChoiceListDrivereLangaugeAlertDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setMapWithCurrentLocation();

    }

    private void setMapWithCurrentLocation() {
        if (mMap != null) {
            String lat = new TawsiliPrefStore(this).getPreferenceValue(Constants.userLastLocationLat);
            String lng = new TawsiliPrefStore(this).getPreferenceValue(Constants.userLastLocationLng);
            if (!lat.trim().isEmpty() && !lng.trim().isEmpty()) {
                setUpMarker(mMap, new LatLng(Double.valueOf(lat), Double.valueOf(lng)));
                try {
                    getAddressInfo(new LatLng(Double.valueOf(lat), Double.valueOf(lng)), curentLocationText);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setUpMarker(GoogleMap mMap, LatLng latLng, LatLng secLatLang) {

        Layout_to_Image layout_to_image;  //Create Object of Layout_to_Image Class
        FrameLayout relativeLayout;   //Define Any Layout
        Bitmap mbitmap;                  //Bitmap for holding Image of layout

        //provide layout with its id in Xml
        relativeLayout = (FrameLayout) findViewById(R.id.orign_marker);
        TextView tv = (TextView) findViewById(R.id.time);
        tv.setText("16\nMin");

        //initialise layout_to_image object with its parent class and pass parameters as (<Current Activity>,<layout object>)
        layout_to_image = new Layout_to_Image(BookABusinessCarActivity.this, relativeLayout);
        //now call the main working function ;) and hold the returned image in bitmap
        mbitmap = layout_to_image.convert_layout();


        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        options.icon(BitmapDescriptorFactory.fromBitmap(mbitmap));
        Marker marker = mMap.addMarker(options);

        marker.showInfoWindow();
        // for second location

        MapHelper.setUpMarker(mMap, secLatLang, R.drawable.flag_marker);


        //animate camera
        updateZoom(mMap, latLng, secLatLang);
    }

    private void setUpMarker(GoogleMap mMap, LatLng latLng) {

        Layout_to_Image layout_to_image;  //Create Object of Layout_to_Image Class
        FrameLayout relativeLayout;   //Define Any Layout
        Bitmap mbitmap;                  //Bitmap for holding Image of layout

        //provide layout with its id in Xml
        relativeLayout = (FrameLayout) findViewById(R.id.orign_marker);
        TextView tv = (TextView) findViewById(R.id.time);
        tv.setText(new TawsiliPrefStore(this).getPreferenceValue(Constants.PREFERENCE_DRIVER_DURATION));

        //initialise layout_to_image object with its parent class and pass parameters as (<Current Activity>,<layout object>)
        layout_to_image = new Layout_to_Image(BookABusinessCarActivity.this, relativeLayout);
        //now call the main working function ;) and hold the returned image in bitmap
        mbitmap = layout_to_image.convert_layout();


        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        options.icon(BitmapDescriptorFactory.fromBitmap(mbitmap));
        Marker marker = mMap.addMarker(options);

        marker.showInfoWindow();


        //animate camera
        updateZoom(mMap, latLng);
    }

    /*
     * Zooms the map to show the area of interest based on the search radius
     */
    private void updateZoom(GoogleMap mMap, LatLng myLatLng, LatLng secLatLang) {
        LatLngBounds egypt = new LatLngBounds(
                myLatLng, secLatLang);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(egypt.southwest, 16));
    }

    private void updateZoom(GoogleMap mMap, LatLng myLatLng) {
        // Zoom to the given bounds
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 14));
        // set draggable false done
        mMap.getUiSettings().setScrollGesturesEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                if (!getIntent().getBooleanExtra("now", false)) {
                    // go to select date and time
                    Intent intent1 = new Intent(this, SelectTimeActivity.class);
                    startActivity(intent1);
                    overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);
                } else {
                    // let go
                    startActivity(new Intent(this, YourRideActivity.class));
                }
                break;
            case R.id.current_location_button:
                Intent intent3 = new Intent(this, FavoritePlacesActivity.class);
                intent3.putExtra(Constants.comingFrom, 101);
                startActivityForResult(intent3, 101);
                overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);
                break;
            case R.id.drop_off_location_button:
                Intent intent4 = new Intent(this, FavoritePlacesActivity.class);
                startActivity(intent4);
                overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);
                break;
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                //---get the result using getIntExtra()---
                mMap.clear();

                double lat = data.getDoubleExtra("lat", 0);
                double lng = data.getDoubleExtra("lng", 0);

                new TawsiliPrefStore(this).addPreference(Constants.userLastLocationLat, String.valueOf(lat));
                new TawsiliPrefStore(this).addPreference(Constants.userLastLocationLng, String.valueOf(lng));
                setMapWithCurrentLocation();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMap != null)
            // set draggable false done
            mMap.getUiSettings().setScrollGesturesEnabled(true);
    }
}
