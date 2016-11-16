package com.perfect_apps.tawsili.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.perfect_apps.tawsili.R;
import com.perfect_apps.tawsili.app.AppController;
import com.perfect_apps.tawsili.models.DriverDurationAndDistance;
import com.perfect_apps.tawsili.models.SchedualObject;
import com.perfect_apps.tawsili.parser.JsonParser;
import com.perfect_apps.tawsili.store.SceduleStore;
import com.perfect_apps.tawsili.store.TawsiliPrefStore;
import com.perfect_apps.tawsili.utils.Constants;
import com.perfect_apps.tawsili.utils.OrderDriver;
import com.perfect_apps.tawsili.utils.TawsiliPublicFunc;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScheduleTesultActivity extends LocalizationActivity implements View.OnClickListener{
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.button1)Button b1;
    @BindView(R.id.button2)Button b2;

    private String mEstimateFare = "0";
    private String distanceWithKilo = "0";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_tesult);
        ButterKnife.bind(this);
        setToolbar();
        changeFontOfText();

        b1.setOnClickListener(this);
        b2.setOnClickListener(this);

        if (getIntent().getStringExtra("flag") != null){
            createOrder();
        }

    }

    private void changeFontOfText() {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/normal.ttf");
        b1.setTypeface(font);
        b2.setTypeface(font);
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button1:
                // create order
                createOrder();
                break;
            case R.id.button2:
                finish();
                break;
        }
    }

   private void createOrder(){
        SchedualObject schedualObject = new SceduleStore(this).findItem(getIntent().getStringExtra("ID"));
        if (schedualObject != null) {
            String mCategoryValue = schedualObject.getOrder_category();
            String mCategoryName;
            switch (mCategoryValue) {
                case "1":
                    mCategoryName = "Economy";
                    break;
                case "2":
                    mCategoryName = "Business";
                    break;
                case "3":
                    mCategoryName = "Vip";
                    break;
                case "4":
                    mCategoryName = "Family_Regular";
                    break;
                case "5":
                    mCategoryName = "Family_Special";
                    break;
                default:
                    mCategoryName = "Economy";
                    break;
            }
            String tripTime = schedualObject.getOrder_start_time();
            String currentLocationInfo = schedualObject.getFrom_details();
            String dropOffLocationInfo = schedualObject.getTo_details();
            String promoCode = schedualObject.getPromocode();

            String lat = schedualObject.getFrom_location_lat();
            String lng = schedualObject.getFrom_location_lng();

            String latDrop = schedualObject.getTo_location_lat();
            String lngDrop = schedualObject.getTo_location_lng();


            new TawsiliPrefStore(this).addPreference(Constants.userLastLocationLat, lat);
            new TawsiliPrefStore(this).addPreference(Constants.userLastLocationLng, lng);

            new TawsiliPrefStore(this).addPreference(Constants.userLastDropOffLocationLat, latDrop);
            new TawsiliPrefStore(this).addPreference(Constants.userLastDropOffLocationLng, lngDrop);

            try {
                if (Double.valueOf(latDrop) > 0
                        && Double.valueOf(lngDrop) > 0
                        && distanceWithKilo.equalsIgnoreCase("0")
                        && mEstimateFare.equalsIgnoreCase("0")){
                    getDriverDurationFromUserAndCalculateFare(lat, lng, latDrop, lngDrop, mCategoryValue);
                    return;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            if (!lat.trim().isEmpty() && !lng.trim().isEmpty()) {
                new OrderDriver(this, mCategoryValue, mCategoryName, currentLocationInfo
                        , dropOffLocationInfo, mEstimateFare, tripTime,
                        "2", distanceWithKilo, mCategoryValue, promoCode, "Later");
            }
        }
    }

    private void getDriverDurationFromUserAndCalculateFare(String userLat, String userLng,
                                                           String driverLat, String driverLng,
                                                           final String mCategoryValue) {
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
                }
                createOrder();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                createOrder();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

}
