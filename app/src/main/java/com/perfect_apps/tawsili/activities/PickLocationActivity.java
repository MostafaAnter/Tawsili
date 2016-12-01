package com.perfect_apps.tawsili.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
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
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.perfect_apps.tawsili.BuildConfig;
import com.perfect_apps.tawsili.R;
import com.perfect_apps.tawsili.app.AppController;
import com.perfect_apps.tawsili.models.DriverDurationAndDistance;
import com.perfect_apps.tawsili.models.DriverModel;
import com.perfect_apps.tawsili.models.NetworkEvent;
import com.perfect_apps.tawsili.models.TouchMapEvent;
import com.perfect_apps.tawsili.parser.JsonParser;
import com.perfect_apps.tawsili.scheduleing_task.GetUserSchedule;
import com.perfect_apps.tawsili.scheduleing_task.PushLocalNotification;
import com.perfect_apps.tawsili.store.TawsiliPrefStore;
import com.perfect_apps.tawsili.utils.Constants;
import com.perfect_apps.tawsili.utils.CustomTypefaceSpan;
import com.perfect_apps.tawsili.utils.MapStateManager;
import com.perfect_apps.tawsili.utils.SweetDialogHelper;
import com.perfect_apps.tawsili.utils.TawsiliPublicFunc;
import com.perfect_apps.tawsili.utils.Utils;
import com.splunk.mint.Mint;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class PickLocationActivity extends LocalizationActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        TabLayout.OnTabSelectedListener, OnMapReadyCallback,
        View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.linearLayout1)
    LinearLayout linearLayout1;

    @BindView(R.id.text5)
    TextView textView5;
    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.button2)
    Button button2;
    @BindView(R.id.search_button)
    LinearLayout searchImageView;
    @BindView(R.id.orign_marker)
    FrameLayout originalMarker;
    @BindView(R.id.time)
    TextView textTime;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.fab)FloatingActionButton fab;


    private GoogleMap mMap;
    private static final int GPS_ERRORDIALOG_REQUEST = 9001;

    private int[] tabIcons = {
            R.drawable.economy,
            R.drawable.business,
            R.drawable.vip,
            R.drawable.family
    };

    // for fetch last location
    GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    // driver object
    private DriverModel driverModel;

    // pass this values to second page
    private String mCategoryName; // category to get all drivers
    private String mCategoryValue; // category to get all drivers

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_location);
        ButterKnife.bind(this);
        setToolbar();
        changeFontOfText();

        // for map
        if (servicesOK()) {
            initMap();
        }

        onCreateTabLayout();
        setupTabIcons();
        setTabLayoutColor();
        changeTabsFont();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        changeFontOfNavigation();

        animateView(linearLayout1);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        searchImageView.setOnClickListener(this);

        // Check if has GPS
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }


        // Create an instance of GoogleAPIClient.
        if (Utils.isOnline(this)) {
            initGoogleApiClient();
        }

        // start service of getting uer schedule
        Intent getUserSchedule = new Intent(this, GetUserSchedule.class);
        startService(getUserSchedule);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMapWithCurrentLocation();
            }
        });

        Mint.initAndStartSession(this.getApplication(), "9331c1d6");
    }

    private void initGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
        // download priceList
        TawsiliPublicFunc.getPriceList(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
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

    private void changeFontOfText() {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/normal.ttf");
        Typeface fontBold = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        textView5.setTypeface(fontBold);
        button1.setTypeface(font);
        button2.setTypeface(font);

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
            startActivity(new Intent(PickLocationActivity.this, MyRidesActivity.class));

        } else if (id == R.id.my_scheduled_rides) {
            startActivity(new Intent(this, MyScheduleOrdersActivity.class));

        } else if (id == R.id.invite_friends) {
            startActivity(new Intent(PickLocationActivity.this, InviteFriendActivity.class));

        } else if (id == R.id.settings) {
            startActivity(new Intent(PickLocationActivity.this, SettingsActivity.class));
        } else if (id == R.id.english_speaking) {
            showSingleChoiceListDrivereLangaugeAlertDialog();
        }else if (id == R.id.call_us){
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "00966920008819"));
            startActivity(callIntent);
        }else if (id == R.id.payment){
            new SweetDialogHelper(this).showTitleWithATextUnder("", "Cash method is available for now");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void onCreateTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.economy));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.business));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.vip));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.family));
        tabLayout.setOnTabSelectedListener(this);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    // set tab bar indicator height
    private void setTabLayoutColor() {
        tabLayout.setSelectedTabIndicatorHeight(0);
    }

    private void changeTabsFont() {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    Typeface makOnWayFont = Typeface.createFromAsset(getAssets(), "fonts/normal.ttf");
                    ((TextView) tabViewChild).setTypeface(makOnWayFont);
                }
            }
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                getDriversList("1");
                mCategoryValue = "1";
                mCategoryName = "Economy";
                // TODO: 11/8/16
                new TawsiliPrefStore(this)
                        .addPreference(Constants.PREFERENCE_ORDER_TYPE, "1");
                break;
            case 1:
                getDriversList("2");
                mCategoryValue = "2";
                mCategoryName = "Business";
                new TawsiliPrefStore(this)
                        .addPreference(Constants.PREFERENCE_ORDER_TYPE, "2");
                break;
            case 2:
                getDriversList("3");
                mCategoryValue = "3";
                mCategoryName = "Vip";
                new TawsiliPrefStore(this)
                        .addPreference(Constants.PREFERENCE_ORDER_TYPE, "3");
                break;
            case 3:
                showSingleChoiceFamilyTypeAlertDialog();
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                getDriversList("1");
                mCategoryValue = "1";
                mCategoryName = "Economy";
                new TawsiliPrefStore(this)
                        .addPreference(Constants.PREFERENCE_ORDER_TYPE, "1");
                break;
            case 1:
                getDriversList("2");
                mCategoryValue = "2";
                mCategoryName = "Business";
                new TawsiliPrefStore(this)
                        .addPreference(Constants.PREFERENCE_ORDER_TYPE, "2");
                break;
            case 2:
                getDriversList("3");
                mCategoryValue = "3";
                mCategoryName = "Vip";
                new TawsiliPrefStore(this)
                        .addPreference(Constants.PREFERENCE_ORDER_TYPE, "3");
                break;
            case 3:
                showSingleChoiceFamilyTypeAlertDialog();
                break;
        }
    }

    // setup map
    public boolean servicesOK() {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, GPS_ERRORDIALOG_REQUEST);
            dialog.show();
            fab.setVisibility(View.GONE);

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
        EventBus.getDefault().unregister(this);
        super.onStop();
        if (mMap != null) {
            MapStateManager mgr = new MapStateManager(this);
            mgr.saveMapState(mMap);
        }
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();

        originalMarker.setVisibility(View.GONE);

    }

    @Subscribe
    public void onMessageEvent(NetworkEvent event) {
        initGoogleApiClient();
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
        // if user is offline show message to active network
        if (!Utils.isOnline(this)) {
            new SweetDialogHelper(this).showErrorMessage(getString(R.string.error),
                    getString(R.string.check_network_connection));
        }else {
           initGoogleApiClient();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    /*
     * Zooms the map to show the area of interest based on the search radius
     */
    private void updateZoom(GoogleMap mMap, LatLng myLatLng) {
        // Zoom to the given bounds
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 14));
    }

    @Override
    public void onClick(View v) {
        // get latLang object of center
        if (mMap != null) {
            LatLng latLng = mMap.getCameraPosition().target;
            // save location inside preference
            new TawsiliPrefStore(this).addPreference(Constants.userLastLocationLat, String.valueOf(latLng.latitude));
            new TawsiliPrefStore(this).addPreference(Constants.userLastLocationLng, String.valueOf(latLng.longitude));

        }
        String lat = new TawsiliPrefStore(this).getPreferenceValue(Constants.userLastLocationLat);
        String lng = new TawsiliPrefStore(this).getPreferenceValue(Constants.userLastLocationLng);
        switch (v.getId()) {
            case R.id.button1:
                if (!lat.trim().isEmpty() && !lng.trim().isEmpty()) {
                    Intent intent = new Intent(this, BookABusinessCarActivity.class);
                    intent.putExtra("now", true);
                    intent.putExtra("CategoryName",mCategoryName);
                    intent.putExtra("CategoryValue", mCategoryValue);
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);
                }else {
                    Toast.makeText(this, "There is no location", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button2:
                if (!lat.trim().isEmpty() && !lng.trim().isEmpty()) {
                    Intent intent2 = new Intent(this, BookABusinessCarActivity.class);
                    intent2.putExtra("now", false);
                    intent2.putExtra("CategoryName",mCategoryName);
                    intent2.putExtra("CategoryValue", mCategoryValue);
                    startActivity(intent2);
                    overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);
                } else {
                    Toast.makeText(this, "There is no location", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.search_button:
                Intent intent3 = new Intent(this, FavoritePlacesActivity.class);
                intent3.putExtra(Constants.comingFrom, 100);
                startActivityForResult(intent3, 100);
                overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);
                break;

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        setMapWithCurrentLocation();

    }

    private void setMapWithCurrentLocation() {
        if (mLastLocation != null && mMap != null && getIntent().getStringExtra(Constants.comingFrom) == null) {
            updateZoom(mMap, new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            originalMarker.setVisibility(View.VISIBLE);
            try {
                getAddressInfo(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // save location inside preference
            new TawsiliPrefStore(this).addPreference(Constants.userLastLocationLat, String.valueOf(mLastLocation.getLatitude()));
            new TawsiliPrefStore(this).addPreference(Constants.userLastLocationLng, String.valueOf(mLastLocation.getLongitude()));
            // load driverData
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            tab.select();
        } else if (mMap != null) {
            String lat = new TawsiliPrefStore(this).getPreferenceValue(Constants.userLastLocationLat);
            String lng = new TawsiliPrefStore(this).getPreferenceValue(Constants.userLastLocationLng);
            if (!lat.trim().isEmpty() && !lng.trim().isEmpty()) {
                updateZoom(mMap, new LatLng(Double.valueOf(lat), Double.valueOf(lng)));
                originalMarker.setVisibility(View.VISIBLE);
                try {
                    getAddressInfo(new LatLng(Double.valueOf(lat), Double.valueOf(lng)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // load driverData
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            tab.select();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void buildAlertMessageNoGps() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.open_gps))
                .setContentText(getString(R.string.why_open_gps))
                .setConfirmText(getString(R.string.yes_open_gps))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                })
                .show();
    }

    private void getAddressInfo(LatLng latLng) throws IOException {

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

        textView5.setText(sb);

        Log.e("address info", sb.toString());

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

    // set up family
    private String mCheckedFamilyTypeItem;

    public void showSingleChoiceFamilyTypeAlertDialog() {
        final String[] list = new String[]{getString(R.string.regular), getString(R.string.special)};
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.category))
                .setSingleChoiceItems(list,
                        -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mCheckedFamilyTypeItem = list[which];
                                if (which == 0) {
                                    dialog.dismiss();
                                    getDriversList("4");
                                    mCategoryValue = "4";
                                    mCategoryName = "Family_Regular";
                                    new TawsiliPrefStore(PickLocationActivity.this)
                                            .addPreference(Constants.PREFERENCE_ORDER_TYPE, "4");
                                } else if (which == 1) {
                                    dialog.dismiss();
                                    getDriversList("5");
                                    mCategoryValue = "5";
                                    mCategoryName = "Family_Special";
                                    new TawsiliPrefStore(PickLocationActivity.this)
                                            .addPreference(Constants.PREFERENCE_ORDER_TYPE, "5");
                                }
                            }
                        })
                .show();
    }

    SweetDialogHelper sweetDialogHelper;
    private void getDriversList(String category) {
        String requestTag = "driversRequest";
        AppController.getInstance().getRequestQueue().cancelAll(requestTag);
        if (sweetDialogHelper != null){
            sweetDialogHelper.dismissDialog();
            sweetDialogHelper = null;
        }

        final String lat = new TawsiliPrefStore(this).getPreferenceValue(Constants.userLastLocationLat);
        final String lng = new TawsiliPrefStore(this).getPreferenceValue(Constants.userLastLocationLng);
        if (!lat.trim().isEmpty() && !lng.trim().isEmpty()) {
            final SweetDialogHelper sdh = new SweetDialogHelper(this);
            sdh.showMaterialProgress(getString(R.string.loading));
            sweetDialogHelper = sdh;
            String url = BuildConfig.API_BASE_URL + "drivers.php?category=" + category + "&language=" +
                    String.valueOf(new TawsiliPrefStore(this)
                            .getIntPreferenceValue(Constants.PREFERENCE_DRIVER_LANGUAGE));
            url = url;
            StringRequest strReq = new StringRequest(Request.Method.GET,
                    url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d("Drivers", response.toString());
                    driverModel = JsonParser.parseDriversList(lat, lng, response);
                    if (driverModel != null) {
                        // get duration with matrix api
                        getDriverDurationFromUser(sdh, lat, lng, driverModel.getCurrent_location_lat(),
                                driverModel.getCurrent_location_lng());
                    } else {
                        sdh.dismissDialog();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Error drivers", "Error: " + error.getMessage());
                    sdh.dismissDialog();
                }
            });
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, requestTag);
        }

    }

    private void getDriverDurationFromUser(final SweetDialogHelper sdh, String userLat, String userLng, String driverLat, String driverLng) {
        String url = TawsiliPublicFunc.createMatrixUri(userLat, userLng, driverLat, driverLng);
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("duration", response.toString());
                List<DriverDurationAndDistance> driverDurationAndDistanceList =
                        JsonParser.parseDistanceAndDuration(response);

                if (driverDurationAndDistanceList != null && driverDurationAndDistanceList.size() > 0) {
                    DriverDurationAndDistance mDriverDurationAndDistance = driverDurationAndDistanceList.get(0);

                    if (Integer.valueOf(mDriverDurationAndDistance.getDurationValue()) > 540) {
                        new TawsiliPrefStore(PickLocationActivity.this)
                                .addPreference(Constants.PREFERENCE_DRIVER_DURATION, "9\nmin");
                        textTime.setText("9\nmin");
                    } else {
                        int dur = Integer.valueOf(mDriverDurationAndDistance.getDurationValue());
                        if (dur >= 60){
                            dur = dur / 60;
                        }else {
                            dur = 1;
                        }
                        new TawsiliPrefStore(PickLocationActivity.this)
                                .addPreference(Constants.PREFERENCE_DRIVER_DURATION, dur + "\nmin");
                        textTime.setText(dur + "\n min");
                    }
                }

                sdh.dismissDialog();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                sdh.dismissDialog();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if (resultCode == RESULT_OK){

            }
        }
    }

    @Subscribe
    public void onMessageEvent(TouchMapEvent event) {
        Log.d("handel touch", "handel touch");
        try {
            LatLng latLng = mMap.getCameraPosition().target;
            getAddressInfo(latLng);
            new TawsiliPrefStore(this).addPreference(Constants.userLastLocationLat, String.valueOf(latLng.latitude));
            new TawsiliPrefStore(this).addPreference(Constants.userLastLocationLng, String.valueOf(latLng.longitude));
            getDriversList(new TawsiliPrefStore(this).getPreferenceValue(Constants.PREFERENCE_ORDER_TYPE));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
