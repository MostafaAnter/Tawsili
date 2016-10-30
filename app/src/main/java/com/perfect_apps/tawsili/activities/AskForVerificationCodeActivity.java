package com.perfect_apps.tawsili.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.perfect_apps.tawsili.BuildConfig;
import com.perfect_apps.tawsili.R;
import com.perfect_apps.tawsili.app.AppController;
import com.perfect_apps.tawsili.models.PickTimeEvent;
import com.perfect_apps.tawsili.models.ReceiveSMSEvent;
import com.perfect_apps.tawsili.store.TawsiliPrefStore;
import com.perfect_apps.tawsili.utils.Constants;
import com.perfect_apps.tawsili.utils.CustomRequest;
import com.perfect_apps.tawsili.utils.SweetDialogHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class AskForVerificationCodeActivity extends LocalizationActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.button1)
    Button button1;

    @BindView(R.id.editText1)
    EditText editText1;

    @BindView(R.id.linearLayout2)
    LinearLayout linearLayout2;
    @BindView(R.id.text1)
    TextView textView1;
    @BindView(R.id.text2)
    TextView textView2;
    @BindView(R.id.button2)
    Button button2;

    public static final String TAG = "AskForVerificationCodeA";
    private String code = "";
    private int counter = 60;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_for_verification_code);

        ButterKnife.bind(this);
        setToolbar();
        changeFontOfText();
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);

        sendConfirmationCode();
        new FakeTask().execute("");
    }

    private void changeFontOfText() {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/normal.ttf");
        button1.setTypeface(font);
        editText1.setTypeface(font);

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
        switch (v.getId()) {
            case R.id.button2:
                sendConfirmationCode();
                new FakeTask().execute("");
                break;
            case R.id.button1:
                if (getIntent().getStringExtra(Constants.comingFrom) == null) {
                    if (editText1.getText().toString().trim().equalsIgnoreCase(code)) {
                        startActivity(new Intent(AskForVerificationCodeActivity.this, PickLocationActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);
                    } else {
                        new SweetDialogHelper(AskForVerificationCodeActivity.this)
                                .showErrorMessage(getString(R.string.error), getString(R.string.invalid_code));
                    }

                } else if (getIntent().getStringExtra(Constants.comingFrom).equalsIgnoreCase(SignUpActivity.TAG)) {
                    if (editText1.getText().toString().trim().equalsIgnoreCase(code)) {
                        registerNormalNewUser();
                    } else {
                        new SweetDialogHelper(AskForVerificationCodeActivity.this)
                                .showErrorMessage(getString(R.string.error), getString(R.string.invalid_code));
                    }

                } else if (getIntent().getStringExtra(Constants.comingFrom).equalsIgnoreCase(AskForEmailActivity.TAG)) {
                    if (editText1.getText().toString().trim().equalsIgnoreCase(code)) {
                        registerFaceBookNewUser();
                    } else {
                        new SweetDialogHelper(AskForVerificationCodeActivity.this)
                                .showErrorMessage(getString(R.string.error), getString(R.string.invalid_code));
                    }
                }
                break;
        }

    }


    private void sendConfirmationCode() {
        String url;

        if (code.trim().isEmpty()) {
            url = BuildConfig.API_BASE_URL + "send.php?number=" + new TawsiliPrefStore(AskForVerificationCodeActivity.this)
                    .getPreferenceValue(Constants.register_mobile);

        } else {
            url = BuildConfig.API_BASE_URL + "send.php?number=" + new TawsiliPrefStore(AskForVerificationCodeActivity.this)
                    .getPreferenceValue(Constants.register_mobile) + "&code=" + code;
        }


        // here should show dialog
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        code = jsonObject.optString("Code");

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

    private void registerNormalNewUser() {

        String url = BuildConfig.API_BASE_URL + "registeruser.php";
        // here should show dialog
        final SweetDialogHelper sdh = new SweetDialogHelper(this);
        sdh.showMaterialProgress(getString(R.string.loading));

        JSONObject params = new JSONObject();
        try {
            params.put("name", new TawsiliPrefStore(AskForVerificationCodeActivity.this)
                    .getPreferenceValue(Constants.register_fullName));
            params.put("mobile", new TawsiliPrefStore(AskForVerificationCodeActivity.this)
                    .getPreferenceValue(Constants.register_mobile));
            params.put("mail", new TawsiliPrefStore(AskForVerificationCodeActivity.this)
                    .getPreferenceValue(Constants.register_email));
            params.put("password", new TawsiliPrefStore(AskForVerificationCodeActivity.this)
                    .getPreferenceValue(Constants.register_password));
            params.put("from", "Android");
            params.put("udid", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CustomRequest strReq = new CustomRequest(Request.Method.POST,
                url, params, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response1) {
                String response = response1.toString();
                Log.d(TAG, response.toString());
                sdh.dismissDialog();

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String result = jsonObject.optString("error");
                        String userId = jsonObject.optString("id");
                        if (!result.equalsIgnoreCase("") && userId.equalsIgnoreCase("")){
                            new SweetDialogHelper(AskForVerificationCodeActivity.this).showBasicMessage(result);
                        }else {
                            new TawsiliPrefStore(AskForVerificationCodeActivity.this).addPreference(Constants.userId, userId);
                            startActivity(new Intent(AskForVerificationCodeActivity.this, PickLocationActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);
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
                sdh.dismissDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void registerFaceBookNewUser() {

        String url = BuildConfig.API_BASE_URL + "registeruser.php";
        // here should show dialog
        final SweetDialogHelper sdh = new SweetDialogHelper(this);
        sdh.showMaterialProgress(getString(R.string.loading));

        JSONObject params = new JSONObject();
        try {
            params.put("name", new TawsiliPrefStore(AskForVerificationCodeActivity.this)
                    .getPreferenceValue(Constants.fbuserName));
            params.put("mobile", new TawsiliPrefStore(AskForVerificationCodeActivity.this)
                    .getPreferenceValue(Constants.register_mobile));
            params.put("mail", new TawsiliPrefStore(AskForVerificationCodeActivity.this)
                    .getPreferenceValue(Constants.fbuserEmail));
            params.put("password", "");
            params.put("from", "Android");
            params.put("udid", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CustomRequest strReq = new CustomRequest(Request.Method.POST,
                url, params, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response1) {

                String response = response1.toString();
                Log.d(TAG, response.toString());
                sdh.dismissDialog();

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String result = jsonObject.optString("error");
                        String userId = jsonObject.optString("id");
                        if (!result.equalsIgnoreCase("") && userId.equalsIgnoreCase("")){
                            new SweetDialogHelper(AskForVerificationCodeActivity.this).showBasicMessage(result);
                        }else {
                            new TawsiliPrefStore(AskForVerificationCodeActivity.this).addPreference(Constants.userId, userId);
                            startActivity(new Intent(AskForVerificationCodeActivity.this, PickLocationActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.toString());
                sdh.dismissDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void hideButton() {
        linearLayout2.setVisibility(View.VISIBLE);
        button2.setVisibility(View.GONE);
        counter = 40;

        textView2.setText(" " + counter);

    }

    private void showButton() {
        linearLayout2.setVisibility(View.GONE);
        button2.setVisibility(View.VISIBLE);
    }

    private class FakeTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            hideButton();
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
            showButton();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

            counter--;
            textView2.setText(" " + counter);


        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onMessageEvent(ReceiveSMSEvent event) {
        String s = event.getMessage();
        editText1.setText(s);

    }
}
