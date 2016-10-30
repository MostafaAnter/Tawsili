package com.perfect_apps.tawsili.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
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
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignUpActivity extends LocalizationActivity implements View.OnClickListener {

    public static final String TAG = "SignUpActivity";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.text1) TextView textView1;
    @BindView(R.id.text2) TextView textView2;

    @BindView(R.id.button1)
    Button button1;

    @BindView(R.id.editText1) EditText editText1;
    @BindView(R.id.editText2) EditText editText2;
    @BindView(R.id.editText3) EditText editText3;
    @BindView(R.id.editText4) EditText editText4;
    @BindView(R.id.editText5) EditText editText5;

    @BindView(R.id.checkbox1)
    CheckBox checkBox1;

    @BindView(R.id.signUpUsingFacebook)
    LinearLayout linearLayout1;

    // post parameters
    private String fullName;
    private String mobile;
    private String email;
    private String password;

    // variables belong to login with facebook
    private List<String> permissions = new ArrayList<String>() {{
        //add permission
        add("public_profile");
        add("email");
    }};

    // initialize callback function that return from login with facebook
    private CallbackManager callbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        setToolbar();
        changeFontOfText();
        // set login with Facebook
        setLoginWithFacebook();
        
        linearLayout1.setOnClickListener(this);
        button1.setOnClickListener(this);
    }

    private void changeFontOfText(){
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/normal.ttf");
        textView1.setTypeface(font);
        textView2.setTypeface(font);

        button1.setTypeface(font);
        editText1.setTypeface(font);
        editText2.setTypeface(font);
        editText3.setTypeface(font);
        editText4.setTypeface(font);
        editText5.setTypeface(font);
        checkBox1.setTypeface(font);

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

    // set login with facebook
    private void loginWithFacebook() {
        if (Utils.isOnline(this)) {
            LoginManager.getInstance().logInWithReadPermissions(this, permissions);
        } else {
            // show error dialog
            new SweetDialogHelper(this).showErrorMessage(getString(R.string.error), getString(R.string.check_network_connection));
        }
    }

    private void setLoginWithFacebook() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        final SweetDialogHelper sdh = new SweetDialogHelper(SignUpActivity.this);
                        sdh.showMaterialProgress(getString(R.string.loading));

                        // App code
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        // Application code
                                        Log.d("graph respon", response.getJSONObject().toString());

                                        parseGraph(response.getJSONObject().toString());
                                        sdh.dismissDialog();
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException exception) {

                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void parseGraph(String graph) {
        JSONObject graphObject = null;
        try {
            graphObject = new JSONObject(graph);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String userId = graphObject.optString("id");
        String userMail = graphObject.optString("email");
        String userName = graphObject.optString("name");
        // here checkUser
        if (userMail != null || !userMail.trim().isEmpty()) {
            new TawsiliPrefStore(this).addPreference(Constants.fbuserEmail, userMail);
            new TawsiliPrefStore(this).addPreference(Constants.fbuserName, userName);
            checkUser(userMail);
        } else {
            new TawsiliPrefStore(this).addPreference(Constants.fbuserEmail, userId);
            new TawsiliPrefStore(this).addPreference(Constants.fbuserName, userName);
            checkUser(userId);
        }
    }

    /**
     * check user if active go to main if existing and  not active go to verification code
     * if not exist go to enter email and mobile
     * if blocked show message to call company
     */
    private void checkUser(String userMail) {

        String url = BuildConfig.API_BASE_URL + "checkuser.php?mail=" + userMail + "&mobile=null";
        // here should show dialog
        final SweetDialogHelper sdh = new SweetDialogHelper(this);
        sdh.showMaterialProgress(getString(R.string.loading));
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                sdh.dismissDialog();

                parseUserCheckParseFeed(response);

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

    private void checkUserToRegister(String userMail, String mMobile) {

        String url = BuildConfig.API_BASE_URL + "checkuser.php?mail=" + userMail + "&mobile=" + mMobile;
        // here should show dialog
        final SweetDialogHelper sdh = new SweetDialogHelper(this);
        sdh.showMaterialProgress(getString(R.string.loading));
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                sdh.dismissDialog();

                parseUserCheckParseFeedToRegister(response);

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

    private void parseUserCheckParseFeed(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String result = jsonObject.optString("result");
                String userId = jsonObject.optString("id");
                String mobile = jsonObject.optString("mobile");
                String email = jsonObject.optString("mail");
                String status = jsonObject.optString("status");
                if (result != null && !result.trim().isEmpty() && result.equalsIgnoreCase(Constants.statusEmpty)) {
                    startActivity(new Intent(SignUpActivity.this, AskForEmailActivity.class));
                } else if (status.equalsIgnoreCase(Constants.statusActive)) {
                    new TawsiliPrefStore(this).addPreference(Constants.userId, userId);
                    startActivity(new Intent(SignUpActivity.this, PickLocationActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);
                } else if (status.equalsIgnoreCase(Constants.statusDeactive)) {
                    new TawsiliPrefStore(this).addPreference(Constants.userId, userId);
                    new TawsiliPrefStore(this).addPreference(Constants.register_mobile, mobile);
                    new TawsiliPrefStore(this).addPreference(Constants.register_email, email);
                    startActivity(new Intent(SignUpActivity.this, AskForVerificationCodeActivity.class));
                } else if (status.equalsIgnoreCase(Constants.statusClosedByClient) ||
                        status.equalsIgnoreCase(Constants.statusClosedBySystem)) {
                    new SweetDialogHelper(SignUpActivity.this).showErrorMessage(getString(R.string.error),
                            "You seem Blocked from system, plz contact Tawsili team");
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseUserCheckParseFeedToRegister(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String result = jsonObject.optString("result");
                String userId = jsonObject.optString("id");
                String status = jsonObject.optString("status");
                if (result != null && !result.trim().isEmpty() && result.equalsIgnoreCase(Constants.statusEmpty)) {
                    Intent intent = new Intent(SignUpActivity.this, AskForVerificationCodeActivity.class);
                    intent.putExtra(Constants.comingFrom, TAG);
                    startActivity(intent);
                } else if (status.equalsIgnoreCase(Constants.statusActive)) {
                    new SweetDialogHelper(SignUpActivity.this)
                            .showBasicMessage("هذا الهاتف مستخدم من قبل , حاول التسجيل ببيانات مختلفة");
                } else if (status.equalsIgnoreCase(Constants.statusDeactive)) {
                    new SweetDialogHelper(SignUpActivity.this)
                            .showBasicMessage("هذا الهاتف مستخدم من قبل , حاول التسجيل ببيانات مختلفة");
                } else if (status.equalsIgnoreCase(Constants.statusClosedByClient) ||
                        status.equalsIgnoreCase(Constants.statusClosedBySystem)) {
                    new SweetDialogHelper(SignUpActivity.this)
                            .showBasicMessage("هذا الهاتف مستخدم من قبل , حاول التسجيل ببيانات مختلفة");
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button1:
                signUpUser();
                break;
            case R.id.signUpUsingFacebook:
                loginWithFacebook();
                break;
        }
    }

    private void signUpUser(){
        if (Utils.isOnline(this)){

            if (checkUserValidData()) {
                new TawsiliPrefStore(this).addPreference(Constants.register_fullName, fullName);
                new TawsiliPrefStore(this).addPreference(Constants.register_email, email);
                new TawsiliPrefStore(this).addPreference(Constants.register_mobile, mobile);
                new TawsiliPrefStore(this).addPreference(Constants.register_password, password);
                checkUserToRegister(email, mobile);
            }
        }else {
            new SweetDialogHelper(this).showErrorMessage(getString(R.string.error), getString(R.string.check_network_connection));
        }

    }

    private boolean checkUserValidData(){
        try {
            fullName = URLEncoder.encode(editText1.getText().toString().trim(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mobile = editText2.getText().toString().trim() + editText3.getText().toString().trim();
        email = editText4.getText().toString().trim();
        password = editText5.getText().toString().trim();

        if (fullName == null || fullName.isEmpty()){
            new SweetDialogHelper(this).showErrorMessage(getString(R.string.error), getString(R.string.enter_name));
            return false;
        }
        if (editText2.getText().toString().trim().isEmpty() ||
                editText3.getText().toString().trim().isEmpty() ||
                !PhoneNumberUtils.isGlobalPhoneNumber(mobile)){
            new SweetDialogHelper(this).showErrorMessage(getString(R.string.error), getString(R.string.phone_not_valid));
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            new SweetDialogHelper(this).showErrorMessage(getString(R.string.error), getString(R.string.email_not_valid));
            return false;
        }
        if (password == null || password.isEmpty() || password.length() < 6){
            new SweetDialogHelper(this).showErrorMessage(getString(R.string.error), getString(R.string.password_not_valid));
            return false;
        }
        if (!checkBox1.isChecked()){
            new SweetDialogHelper(this).showErrorMessage(getString(R.string.error), getString(R.string.terms_and_conditions_not_valid));
            return false;
        }

        return true;

    }
}
