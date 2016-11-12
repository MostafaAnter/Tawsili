package com.perfect_apps.tawsili.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatRatingBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.perfect_apps.tawsili.BuildConfig;
import com.perfect_apps.tawsili.R;
import com.perfect_apps.tawsili.app.AppController;
import com.perfect_apps.tawsili.utils.SweetDialogHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mostafa_anter on 11/12/16.
 */

public class DriverInfoDialog extends DialogFragment implements View.OnClickListener{
    int mNum;
    @BindView(R.id.avatar)CircleImageView avatar;
    @BindView(R.id.driverName)
    TextView driverName;
    @BindView(R.id.car_name)TextView carName;
    @BindView(R.id.license)TextView licensePlate;
    @BindView(R.id.rateValue)TextView rateValue;
    @BindView(R.id.ratingBar)AppCompatRatingBar ratingBar;

    @BindView(R.id.button1)Button button1;
    @BindView(R.id.button2)Button button2;

    private String driverMobiel;
    private String driverId;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static DriverInfoDialog newInstance(int num) {
        DriverInfoDialog f = new DriverInfoDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NO_TITLE, theme = android.R.style.Theme_Holo_Light_Dialog;
        setStyle(style, theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.driver_dialog_info, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        driverId = getArguments().getString("driverId");
        getDriver(driverId);
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button1:
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + driverMobiel));
                startActivity(callIntent);
                break;
            case R.id.button2:
                dismiss();
                break;
        }
    }

    private void getDriver(String driverId){
        final SweetDialogHelper sdh = new SweetDialogHelper(getActivity());
        sdh.showMaterialProgress(getString(R.string.loading));
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
                        licensePlate.setText(license_plate);
                        rateValue.setText(rate);
                        ratingBar.setRating(Float.valueOf(rate));
                        // populate mainImage
                        Glide.with(getActivity())
                                .load("http://tawsely.com/img/drivers/" + img_name)
                                .placeholder(R.color.gray_btn_bg_color)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .crossFade()
                                .dontAnimate()
                                .thumbnail(0.2f)
                                .into(avatar);

                        sdh.dismissDialog();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    sdh.dismissDialog();

                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("checkOrder", "Error: " + error.getMessage());
                sdh.dismissDialog();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }
}
