package com.perfect_apps.tawsili.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.perfect_apps.tawsili.BuildConfig;
import com.perfect_apps.tawsili.R;
import com.perfect_apps.tawsili.app.AppController;
import com.perfect_apps.tawsili.store.TawsiliPrefStore;
import com.perfect_apps.tawsili.utils.Constants;
import com.perfect_apps.tawsili.utils.SweetDialogHelper;
import com.perfect_apps.tawsili.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AskForPromoCodeActivity extends LocalizationActivity implements View.OnClickListener {
    private static final String TAG = "AskForPromoCodeActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.button1)
    Button button1;

    @BindView(R.id.editText1)
    EditText editText1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_for_promo_code);
        ButterKnife.bind(this);
        setToolbar();
        button1.setOnClickListener(this);

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

        if (checkEditText()){
            checkPromo();
        }

    }

    private boolean checkEditText(){
        return !editText1.getText().toString().trim().isEmpty();
    }

    private void checkPromo(){
        String url = BuildConfig.API_BASE_URL + "checkpromocode.php?client=" +
                new TawsiliPrefStore(this).getPreferenceValue(Constants.userId) +
                "&time=" + Utils.returnTime().replace(" ", "%20") + "&promo=" + editText1.getText().toString().trim();
        url = url.replace(" ", "%20");
        // here should show dialog
        final SweetDialogHelper sdh = new SweetDialogHelper(this);
        sdh.showMaterialProgress(getString(R.string.loading));
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
                    String result = jsonObject.optString("result");

                    if (!result.trim().isEmpty()){
                        Intent intent = new Intent();
                        intent.putExtra("result", result);
                        intent.putExtra("promoCode", editText1.getText().toString().trim());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                sdh.dismissDialog();



            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                new SweetDialogHelper(AskForPromoCodeActivity.this)
                        .showErrorMessage(getString(R.string.error), getString(R.string.try_agin));
                sdh.dismissDialog();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);

    }
}
