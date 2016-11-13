package com.perfect_apps.tawsili.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.perfect_apps.tawsili.BuildConfig;
import com.perfect_apps.tawsili.R;
import com.perfect_apps.tawsili.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by mostafa on 07/07/16.
 */
public class RatingDialogFragment extends DialogFragment implements View.OnClickListener {
    int mNum;


    @BindView(R.id.text1)
    TextView textView1;
    @BindView(R.id.closeDialog)
    ImageView close;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.button1)
    Button button1;

    // Container Activity must implement this interface
    public interface OnRateDone {
        public void onRateComplete();
    }


    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static RatingDialogFragment newInstance(int num) {
        RatingDialogFragment f = new RatingDialogFragment();

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
        View v = inflater.inflate(R.layout.fragment_dialog_rate, container, false);
        ButterKnife.bind(this, v);
        changeTextFont();

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        close.setOnClickListener(this);
        button1.setOnClickListener(this);

    }

    private void changeTextFont() {
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/normal.ttf");
        Typeface fontBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bold.ttf");
        textView1.setTypeface(fontBold);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeDialog:
                dismiss();
                break;
            case R.id.button1:
                doRate(getArguments().getString("orderID"));
                break;
        }
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


    private void doRate(String orderID) {
        // Set up a progress dialog
        final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(getString(R.string.loading));
        pDialog.setCancelable(false);
        pDialog.show();


        String url = BuildConfig.API_BASE_URL + "updateorderrate.php?order="
                + orderID + "&note=" + "&rate=" + ratingBar.getRating();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("checkOrder", response.toString());

                pDialog.dismissWithAnimation();
                dismiss();
                new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                        .setContentText(getString(R.string.done))
                        .show();
                dismiss();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("checkOrder", "Error: " + error.getMessage());
                pDialog.dismissWithAnimation();
                dismiss();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);


    }
}
