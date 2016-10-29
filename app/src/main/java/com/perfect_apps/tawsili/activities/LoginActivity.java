package com.perfect_apps.tawsili.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.perfect_apps.tawsili.R;
import com.perfect_apps.tawsili.store.TawsiliPrefStore;
import com.perfect_apps.tawsili.utils.Constants;
import com.perfect_apps.tawsili.utils.SweetDialogHelper;
import com.perfect_apps.tawsili.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends LocalizationActivity implements View.OnClickListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.text1)
    TextView textView1;
    @BindView(R.id.text2)
    TextView textView2;
    @BindView(R.id.text3)
    TextView textView3;

    @BindView(R.id.button1)
    Button button1;

    @BindView(R.id.editText1)
    EditText editText1;
    @BindView(R.id.editText2)
    EditText editText2;

    @BindView(R.id.login_with_facebook)
    LinearLayout linearLayoutLoginWithFaceBook;

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
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setToolbar();
        changeFontOfText();
        // set login with Facebook
        setLoginWithFacebook();

        linearLayoutLoginWithFaceBook.setOnClickListener(this);
        button1.setOnClickListener(this);
    }

    private void changeFontOfText() {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/normal.ttf");
        textView1.setTypeface(font);
        textView2.setTypeface(font);
        textView3.setTypeface(font);

        button1.setTypeface(font);
        editText1.setTypeface(font);
        editText2.setTypeface(font);

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
        if (Utils.isOnline(LoginActivity.this)) {
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

                        final SweetDialogHelper sdh = new SweetDialogHelper(LoginActivity.this);
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
                                        Log.d("graph respon", response.toString());
                                        parseGraph(response.toString());
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
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(graph);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject graphObject = jsonObject.optJSONObject("graphObject");
        String userId = graphObject.optString("id");
        String userMail = graphObject.optString("email");
        String userName = graphObject.optString("name");

        new TawsiliPrefStore(this).addPreference(Constants.userId, userId);
        new TawsiliPrefStore(this).addPreference(Constants.userEmail, userMail);
        new TawsiliPrefStore(this).addPreference(Constants.userName, userName);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button1:
                startActivity(new Intent(LoginActivity.this, PickLocationActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                overridePendingTransition(R.anim.push_up_enter, R.anim.push_up_exit);
                break;
            case R.id.login_with_facebook:
                loginWithFacebook();
                break;
        }

    }

}
