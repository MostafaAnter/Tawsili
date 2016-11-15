package com.perfect_apps.tawsili.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.perfect_apps.tawsili.BuildConfig;
import com.perfect_apps.tawsili.R;
import com.perfect_apps.tawsili.app.AppController;
import com.perfect_apps.tawsili.models.DriverDurationAndDistance;
import com.perfect_apps.tawsili.models.FavoritePlaceItem;
import com.perfect_apps.tawsili.parser.JsonParser;
import com.perfect_apps.tawsili.store.FavoritePlacesStore;
import com.perfect_apps.tawsili.store.TawsiliPrefStore;
import com.perfect_apps.tawsili.utils.Constants;
import com.perfect_apps.tawsili.utils.CustomTypefaceSpan;
import com.perfect_apps.tawsili.utils.MapHelper;
import com.perfect_apps.tawsili.utils.MapStateManager;
import com.perfect_apps.tawsili.utils.OrderDriver;
import com.perfect_apps.tawsili.utils.SweetDialogHelper;
import com.perfect_apps.tawsili.utils.TawsiliPublicFunc;
import com.perfect_apps.tawsili.utils.Utils;
import com.vipul.hp_hp.library.Layout_to_Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookABusinessCarActivity extends LocalizationActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, View.OnClickListener {

    private static final String TAG = "BookABusinessCarActivit";

    // belong like button animations
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);
    private final Map<ImageView, AnimatorSet> likeAnimations = new HashMap<>();

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

    // penalty
    @BindView(R.id.penaltyView)
    LinearLayout penaltyView;
    @BindView(R.id.penaltyValueText)
    TextView penaltyTextView;

    // fare estimate
    @BindView(R.id.fairEstimateView)
    LinearLayout fareEstimateView;
    @BindView(R.id.fairEstimateTitle)
    TextView fairEstimatetitl;
    @BindView(R.id.fairEstimateValue)
    TextView fairEstimatevalue;

    @BindView(R.id.add_current_loc_to_favorite)ImageView currentFavImage;
    @BindView(R.id.add_drop_off_loc_to_favorite)ImageView dropOffFavImage;



    private GoogleMap mMap;
    private static final int GPS_ERRORDIALOG_REQUEST = 9001;

    // for draw markers
    private List<Marker> markers;


    // parameters for order
    private String result = "0";
    private String penalty = "";


    private String mCategoryName; // category to get all drivers
    private String mCategoryValue; // category to get all drivers
    private String promoCode = "NULL";
    private String mEstimateFare = "0";
    private String distanceWithKilo = "0";

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

        // setup markers
        this.markers = new ArrayList<>();

        mCategoryName = getIntent().getStringExtra("CategoryName");
        mCategoryValue = getIntent().getStringExtra("CategoryValue");


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
        fareEstimateView.setOnClickListener(this);

        currentFavImage.setOnClickListener(this);
        dropOffFavImage.setOnClickListener(this);
        // get user to get the penalty
        getUser();
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

        // clear dropOff location
        new TawsiliPrefStore(this).removePreference(Constants.userLastDropOffLocationLat);
        new TawsiliPrefStore(this).removePreference(Constants.userLastDropOffLocationLng);
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

                if (new FavoritePlacesStore(BookABusinessCarActivity.this)
                        .isFavoritItem(lat + "," + lng)){
                    currentFavImage.setImageResource(R.drawable.ic_grade_black_24dp);
                }
            }
        }
    }

    private void setMapWithDropOfftLocation() {
        if (mMap != null) {
            String lat = new TawsiliPrefStore(this).getPreferenceValue(Constants.userLastDropOffLocationLat);
            String lng = new TawsiliPrefStore(this).getPreferenceValue(Constants.userLastDropOffLocationLng);
            if (!lat.trim().isEmpty() && !lng.trim().isEmpty()) {
                Marker marker = MapHelper.setUpMarkerAndReturnMarker(mMap, new LatLng(Double.valueOf(lat), Double.valueOf(lng)), R.drawable.flag_marker);
                markers.add(marker);
                try {
                    getAddressInfo(new LatLng(Double.valueOf(lat), Double.valueOf(lng)), dropOffLocationText);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (new FavoritePlacesStore(BookABusinessCarActivity.this)
                        .isFavoritItem(lat + "," + lng)){
                    dropOffFavImage.setImageResource(R.drawable.ic_grade_black_24dp);
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

        // add to marker list
        markers.add(marker);

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
                    intent1.putExtra("promoCode", promoCode);
                    intent1.putExtra("discount", result);
                    intent1.putExtra("carType", mCategoryValue);
                    intent1.putExtra("toLat", new TawsiliPrefStore(this)
                            .getPreferenceValue(Constants.userLastDropOffLocationLat));
                    intent1.putExtra("toLng", new TawsiliPrefStore(this)
                            .getPreferenceValue(Constants.userLastDropOffLocationLng));
                    intent1.putExtra("fromDetails", curentLocationText.getText().toString().trim());
                    intent1.putExtra("toDetails", dropOffLocationText.getText().toString().trim());
                    startActivity(intent1);
                    overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);
                } else {
                    // let go
                   // startActivity(new Intent(this, YourRideActivity.class));
                    createOder();
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
                intent4.putExtra(Constants.comingFrom, 102);
                startActivityForResult(intent4, 102);
                overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);
                break;
            case R.id.fairEstimateView:
                Intent intent5 = new Intent(this, FavoritePlacesActivity.class);
                intent5.putExtra(Constants.comingFrom, 102);
                startActivityForResult(intent5, 102);
                overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);
                break;
            case R.id.add_promo_code:
                Intent intent6 = new Intent(this, AskForPromoCodeActivity.class);
                startActivityForResult(intent6, 500);
                overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);
                break;
            case R.id.add_drop_off_loc_to_favorite:
                FavoritePlaceItem favoritePlaceItem = new FavoritePlaceItem(dropOffLocationText.getText().toString(),
                        "", new TawsiliPrefStore(BookABusinessCarActivity.this).getPreferenceValue(Constants.userLastDropOffLocationLat),
                        new TawsiliPrefStore(BookABusinessCarActivity.this).getPreferenceValue(Constants.userLastDropOffLocationLng), true);
                updateHeartImage(dropOffFavImage, true, favoritePlaceItem);
                break;
            case R.id.add_current_loc_to_favorite:
                FavoritePlaceItem favoritePlaceItem1 = new FavoritePlaceItem(curentLocationText.getText().toString(),
                        "", new TawsiliPrefStore(BookABusinessCarActivity.this).getPreferenceValue(Constants.userLastLocationLat),
                        new TawsiliPrefStore(BookABusinessCarActivity.this).getPreferenceValue(Constants.userLastLocationLng), true);
                updateHeartImage(currentFavImage, true, favoritePlaceItem1);
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
                if (mMap != null)
                    // set draggable false done
                    mMap.getUiSettings().setScrollGesturesEnabled(true);
                mMap.clear();

                double lat = data.getDoubleExtra("lat", 0);
                double lng = data.getDoubleExtra("lng", 0);

                new TawsiliPrefStore(this).addPreference(Constants.userLastLocationLat, String.valueOf(lat));
                new TawsiliPrefStore(this).addPreference(Constants.userLastLocationLng, String.valueOf(lng));
                setMapWithCurrentLocation();
            }
        } else if (requestCode == 102) {
            if (resultCode == RESULT_OK) {
                // set some view visible
                pickDropOffLocation.setVisibility(View.VISIBLE);
                lineSeperator.setVisibility(View.VISIBLE);
                if (!data.getBooleanExtra("guideTheDriver", false)) {
                    // clear flag marker
                    if (markers.size() > 1) {
                        markers.get(1).remove();
                        markers.remove(1);
                    }

                    //---get the result using getIntExtra()---
                    double lat = data.getDoubleExtra("lat", 0);
                    double lng = data.getDoubleExtra("lng", 0);
                    new TawsiliPrefStore(this).addPreference(Constants.userLastDropOffLocationLat, String.valueOf(lat));
                    new TawsiliPrefStore(this).addPreference(Constants.userLastDropOffLocationLng, String.valueOf(lng));
                    setMapWithDropOfftLocation();
                    centerAllMarker();
                    getDriverDurationFromUserAndCalculateFare(new TawsiliPrefStore(this)
                            .getPreferenceValue(Constants.userLastLocationLat),
                            new TawsiliPrefStore(this)
                                    .getPreferenceValue(Constants.userLastLocationLng),
                            new TawsiliPrefStore(this)
                                    .getPreferenceValue(Constants.userLastDropOffLocationLat),
                            new TawsiliPrefStore(this)
                                    .getPreferenceValue(Constants.userLastDropOffLocationLng));
                } else {
                    // set view gone and clear
                    pickDropOffLocation.setVisibility(View.GONE);
                    lineSeperator.setVisibility(View.GONE);

                    // clear dropOff location
                    new TawsiliPrefStore(this).removePreference(Constants.userLastDropOffLocationLat);
                    new TawsiliPrefStore(this).removePreference(Constants.userLastDropOffLocationLng);

                    // clear dropOff editText
                    dropOffLocationText.setText("");

                    // clear flag marker
                    if (markers.size() > 1) {
                        markers.get(1).remove();
                        markers.remove(1);
                    }

                    updateZoom(mMap, markers.get(0).getPosition());

                }
            }
        } else if (requestCode == 500) {
            if (resultCode == RESULT_OK) {

                if (data.getStringExtra("result") != null) {
                    result = data.getStringExtra("result");
                    promoCode = data.getStringExtra("promoCode");
                    addPromoCode.setText(getString(R.string.valide_promo));
                    addPromoCode.setTextColor(Color.GREEN);
                } else {
                    addPromoCode.setText(getString(R.string.invalide_promo));
                    addPromoCode.setTextColor(Color.RED);
                }


            }
        }
    }

    private void centerAllMarker() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        mMap.setPadding(200, 150, 200, 500);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
        mMap.animateCamera(cu);


    }

    // belong like button
    private void updateHeartImage(final ImageView holder,
                                  boolean animated,
                                  final FavoritePlaceItem favoritePlaceItem) {
        if (animated) {
            if (!likeAnimations.containsKey(holder) &&! new FavoritePlacesStore(BookABusinessCarActivity.this)
                    .isFavoritItem(favoritePlaceItem.getLat() + "," + favoritePlaceItem.getLng())) {
                AnimatorSet animatorSet = new AnimatorSet();
                likeAnimations.put(holder, animatorSet);

                ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder, "scaleX", 0.2f, 1f);
                bounceAnimX.setDuration(300);
                bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

                ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder, "scaleY", 0.2f, 1f);
                bounceAnimY.setDuration(300);
                bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                bounceAnimY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        holder.setImageResource(R.drawable.ic_grade_black_24dp);
                    }
                });
                animatorSet.play(bounceAnimX).with(bounceAnimY);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!favoritePlaceItem.getName().trim().isEmpty()){
                            TawsiliPublicFunc.addItem(BookABusinessCarActivity.this,
                                    favoritePlaceItem);
                        }
                    }
                });

                animatorSet.start();
            } else {
                AnimatorSet animatorSet = new AnimatorSet();
                likeAnimations.remove(holder);

                ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder, "scaleX", 0.2f, 1f);
                bounceAnimX.setDuration(300);
                bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

                ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder, "scaleY", 0.2f, 1f);
                bounceAnimY.setDuration(300);
                bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                bounceAnimY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        holder.setImageResource(R.drawable.ic_grade_gray);
                    }
                });
                animatorSet.play(bounceAnimX).with(bounceAnimY);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (new FavoritePlacesStore(BookABusinessCarActivity.this)
                                .isFavoritItem(favoritePlaceItem.getLat() + "," + favoritePlaceItem.getLng())){
                            TawsiliPublicFunc.removeItem(BookABusinessCarActivity.this,
                                    favoritePlaceItem);
                        }

                    }
                });

                animatorSet.start();
            }
        }
    }

    private void getUser() {
        String url = BuildConfig.API_BASE_URL + "getuser.php?mail=" +
                "&id=" + new TawsiliPrefStore(this).getPreferenceValue(Constants.userId);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String result = jsonObject.optString("balance");

                        if (!result.trim().isEmpty()) {
                            double balance = Double.valueOf(result);
                            if (balance < 0) {
                                penaltyView.setVisibility(View.VISIBLE);
                                penaltyTextView.setText(result.replace("-", ""));
                                penalty = result.replace("-", "");
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void getDriverDurationFromUserAndCalculateFare(String userLat, String userLng,
                                                           String driverLat, String driverLng) {
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
                    int disInKilo = Double.valueOf(mDriverDurationAndDistance.getDistanceValue()).intValue()/1000;
                    distanceWithKilo = String.valueOf(disInKilo);
                    mEstimateFare = String.valueOf(TawsiliPublicFunc.calculateFairEstimate(mCategoryValue, disInKilo,true));
                    int esimatePlusFive = Integer.valueOf(mEstimateFare) + 5;
                    String fareEstimateText = "SR " + mEstimateFare + "-" + esimatePlusFive;
                    fairEstimatevalue.setText(fareEstimateText);
                    new TawsiliPrefStore(BookABusinessCarActivity.this).addPreference(Constants.PREFERENCE_ESTIMATE,
                            fareEstimateText);

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void createOder(){
        String lat = new TawsiliPrefStore(this).getPreferenceValue(Constants.userLastLocationLat);
        String lng = new TawsiliPrefStore(this).getPreferenceValue(Constants.userLastLocationLng);
        if (!lat.trim().isEmpty() && !lng.trim().isEmpty()) {
            new OrderDriver(this, mCategoryValue, mCategoryName, curentLocationText.getText().toString()
            , dropOffLocationText.getText().toString(), mEstimateFare, Utils.returnTime(),
                    "1", distanceWithKilo, mCategoryValue, promoCode, "Now");
        }
    }


}
