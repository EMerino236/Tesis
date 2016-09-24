package com.app.tesis.eduardo.tesis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.tesis.eduardo.tesis.services.AccessServiceAPI;
import com.app.tesis.eduardo.tesis.utils.Constants;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eduardo on 12/07/2016.
 */
public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    Profile profile;
    Integer userId;
    String fullname;
    String email;
    String fbId;
    String fbFullname;
    String fbEmail;
    String fbGender;
    Boolean post_as_anonymous;
    EditText email_txt;
    EditText password_txt;
    TextView email_error_lbl;
    TextView password_error_lbl;
    Button login_button;
    TextView register_txt;
    TextView recover_txt;
    TextView terms_txt;
    TextView privacy_txt;
    // Facebook login button
    private LoginButton fbLoginButton;
    private FacebookCallback<LoginResult> callback;
    // Webservices
    private AccessServiceAPI m_ServiceAccess;
    JSONObject jObjResult;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d("TEST","onCreate");
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        m_ServiceAccess = new AccessServiceAPI();
        email_error_lbl = (TextView) findViewById(R.id.email_error_lbl);
        password_error_lbl = (TextView) findViewById(R.id.password_error_lbl);
        email_txt = (EditText) findViewById(R.id.email_txt);
        password_txt = (EditText) findViewById(R.id.password_txt);
        login_button = (Button) findViewById(R.id.login_button);
        // Progress dialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.progress_dialog_user_message));
        mProgressDialog.setTitle(R.string.progress_dialog_title);
        setTextButtons();
        // Set trackers
        setTrackers();
        // Set login button
        setLoginButton();
        setFbLoginButton();
    }

    public void setTextButtons(){
        String registerTitle = new String(getString(R.string.register_title));
        SpannableString contentRegister = new SpannableString(registerTitle);
        contentRegister.setSpan(new UnderlineSpan(), 0, registerTitle.length(), 0);
        register_txt = (TextView) findViewById(R.id.register_txt);
        register_txt.setText(contentRegister);

        String recoverTitle = new String(getString(R.string.recover_pass_title));
        SpannableString contentRecover = new SpannableString(recoverTitle);
        contentRecover.setSpan(new UnderlineSpan(), 0, recoverTitle.length(), 0);
        recover_txt = (TextView) findViewById(R.id.recover_txt);
        recover_txt.setText(contentRecover);

        String termsTitle = new String(getString(R.string.tc_title));
        SpannableString contentTerms = new SpannableString(termsTitle);
        contentTerms.setSpan(new UnderlineSpan(), 0, termsTitle.length(), 0);
        terms_txt = (TextView) findViewById(R.id.terms_txt);
        terms_txt.setText(contentTerms);

        String privacyTitle = new String(getString(R.string.pp_title));
        SpannableString contentPrivacy = new SpannableString(privacyTitle);
        contentPrivacy.setSpan(new UnderlineSpan(), 0, privacyTitle.length(), 0);
        privacy_txt = (TextView) findViewById(R.id.privacy_txt);
        privacy_txt.setText(contentPrivacy);

        register_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(register);
            }
        });
        recover_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recover = new Intent(LoginActivity.this, RecoverPassActivity.class);
                startActivity(recover);
            }
        });
        terms_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent terms = new Intent(LoginActivity.this, TermsActivity.class);
                startActivity(terms);
            }
        });
        privacy_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent privacy = new Intent(LoginActivity.this, PrivacyActivity.class);
                startActivity(privacy);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout_menu_login:
                Toast.makeText(LoginActivity.this,R.string.menu_same_section,Toast.LENGTH_SHORT).show();
                return true;
            case R.id.logout_menu_register:
                Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(register);
                finish();
                return true;
            case R.id.logout_menu_recover_password:
                Intent recover = new Intent(LoginActivity.this, RecoverPassActivity.class);
                startActivity(recover);
                finish();
                return true;
            case R.id.logout_menu_terms:
                Intent terms = new Intent(LoginActivity.this, TermsActivity.class);
                startActivity(terms);
                finish();
                return true;
            case R.id.logout_menu_privacy:
                Intent privacy = new Intent(LoginActivity.this, PrivacyActivity.class);
                startActivity(privacy);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TEST","onResume");
        LoginManager.getInstance().logOut();
        //after_fb_login();
        //Facebook login
        //profile = Profile.getCurrentProfile();
        //nextActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TEST","onPause");
    }

    protected void onStop() {
        super.onStop();
        Log.d("TEST","onStop");
        //Facebook login
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        //Facebook login
        callbackManager.onActivityResult(requestCode, responseCode, intent);

    }

    private void setTrackers(){
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                Log.d("TEST","onCurrentProfileChanged");
                //profile = newProfile;
                //nextActivity();
            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();
    }

    public void setLoginButton(){
        login_button = (Button) findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate_form()){
                    new TaskLogin(mProgressDialog,LoginActivity.this).execute(email_txt.getText().toString(),password_txt.getText().toString());
                }
            }
        });
    }

    public class TaskLogin extends AsyncTask<String, Void, Integer> {

        ProgressDialog progressDialog;
        LoginActivity activity;
        public TaskLogin(ProgressDialog mProgressDialog, LoginActivity act){
            this.progressDialog = mProgressDialog;
            this.activity = act;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            //Create data to pass in param
            Map<String, String> param = new HashMap<>();
            param.put("email", params[0]);
            param.put("password", params[1]);

            try {
                jObjResult = m_ServiceAccess.convertJSONString2Obj(m_ServiceAccess.getJSONStringWithParam_GET(Constants.ENDPOINT_URL+Constants.AUTH_SERVICE,param));
                if(jObjResult.getBoolean("error")){
                    //Log.d("LOGIN","ERROR");
                    return Constants.ENDPOINT_ERROR;
                }
                userId = jObjResult.getJSONObject("citizen").getInt("id");
                fullname = jObjResult.getJSONObject("citizen").getString("fullname");
                email = jObjResult.getJSONObject("citizen").getString("email");
                post_as_anonymous = jObjResult.getJSONObject("citizen").getBoolean("post_as_anonymous");
                return Constants.ENDPOINT_SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
                return Constants.ENDPOINT_ERROR;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result == Constants.ENDPOINT_ERROR){
                try {
                    String message = jObjResult.getString("message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), R.string.register_error, Toast.LENGTH_LONG).show();
                }
            }else if(result == Constants.ENDPOINT_SUCCESS){
                after_login();
            }
        }
    }

    private void after_login(){
        Intent main = new Intent(LoginActivity.this, MainActivity.class);
        //main.putExtra("profile",profile);
        main.putExtra("login_method","normal");
        main.putExtra("userId",userId);
        main.putExtra("fullname",fullname);
        main.putExtra("email",email);
        main.putExtra("post_as_anonymous",post_as_anonymous);
        startActivity(main);
        finish();
    }

    private boolean validate_form(){
        boolean is_correct = true;
        email_error_lbl.setText(null);
        password_error_lbl.setText(null);
        if(email_txt.getText().length() == 0){
            email_error_lbl.setText(R.string.required_field);
            is_correct = false;
        }
        if(password_txt.getText().length() == 0){
            password_error_lbl.setText(R.string.required_field);
            is_correct = false;
        }
        return is_correct;
    }

    private void setFbLoginButton(){
        fbLoginButton = (LoginButton)findViewById(R.id.fb_login_button);
        callback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                //Log.d("TOKEN2", accessToken.getToken());
                profile = Profile.getCurrentProfile();
                //fbId = profile.getId();
                //fbFullname = profile.getName();
                // Graph request
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender");
                final GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.d("TEST", response.toString());
                                // Application code
                                try {
                                    fbId = object.getString("id");
                                    fbFullname = object.getString("name");
                                    fbEmail = object.getString("email");
                                    fbGender = object.getString("gender");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                request.setParameters(parameters);
                // Run graph request.
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        GraphResponse gResponse = request.executeAndWait();
                    }
                });
                t.start();
                try {
                    t.join();
                    //Log.d("TEST","Entering TaskLogin");
                    new TaskFbLogin(mProgressDialog,LoginActivity.this).execute(fbId,fbFullname,fbEmail,accessToken.getToken(),fbGender);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), R.string.facebook_login_cancelled, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(getApplicationContext(), R.string.facebook_login_exception, Toast.LENGTH_SHORT).show();
            }
        };
        fbLoginButton.setReadPermissions(Arrays.asList("public_profile","email"));
        fbLoginButton.registerCallback(callbackManager, callback);
    }

    public class TaskFbLogin extends AsyncTask<String, Void, Integer> {
        ProgressDialog progressDialog;
        LoginActivity activity;
        public TaskFbLogin(ProgressDialog mProgressDialog, LoginActivity act){
            this.progressDialog = mProgressDialog;
            this.activity = act;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            //Create data to pass in param
            Map<String, String> param = new HashMap<>();
            param.put("facebook_id", params[0]);
            param.put("fullname", params[1]);
            param.put("email", params[2]);
            param.put("session_token", params[3]);
            param.put("gender", params[4]);

            try {
                jObjResult = m_ServiceAccess.convertJSONString2Obj(m_ServiceAccess.getJSONStringWithParam_POST(Constants.ENDPOINT_URL+Constants.FB_AUTH_SERVICE,param));
                if(jObjResult.getBoolean("error")){
                    return Constants.ENDPOINT_ERROR;
                }
                post_as_anonymous = jObjResult.getJSONObject("citizen").getBoolean("post_as_anonymous");
                userId = jObjResult.getJSONObject("citizen").getInt("id");
                return Constants.ENDPOINT_SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
                return Constants.ENDPOINT_ERROR;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result == Constants.ENDPOINT_ERROR){
                try {
                    String message = jObjResult.getString("message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), R.string.register_error, Toast.LENGTH_LONG).show();
                }
            }else if(result == Constants.ENDPOINT_SUCCESS){
                after_fb_login();
            }
        }
    }

    private void after_fb_login(){
        //if(profile != null){
            Intent main = new Intent(LoginActivity.this, MainActivity.class);
            //main.putExtra("profile",profile);
            main.putExtra("login_method","fb");
            main.putExtra("userId",userId);
            main.putExtra("fbId",fbId);
            main.putExtra("fullname",fbFullname);
            main.putExtra("email",fbEmail);
            main.putExtra("post_as_anonymous",post_as_anonymous);
            startActivity(main);
            finish();
        //}
        //LoginManager.getInstance().logOut();
    }
}
