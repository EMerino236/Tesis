package com.app.tesis.eduardo.tesis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    String fbId;
    String fbFullname;
    String fbEmail;
    String fbGender;
    // Facebook login button
    private LoginButton loginButton;
    private FacebookCallback<LoginResult> callback;
    // Webservices
    private AccessServiceAPI m_ServiceAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TEST","onCreate");
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        m_ServiceAccess = new AccessServiceAPI();
        // Set trackers
        setTrackers();
        // Set login button
        setLoginButton();
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TEST","onResume");
        //Facebook login
        profile = Profile.getCurrentProfile();
        nextActivity();
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

    private void setLoginButton(){
        loginButton = (LoginButton)findViewById(R.id.login_button);
        callback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Log.d("TOKEN2", accessToken.getToken());
                profile = Profile.getCurrentProfile();
                fbId = profile.getId();
                fbFullname = profile.getName();
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
                    Log.d("TEST","Entering TaskLogin");
                    new TaskLogin().execute(fbId,fbFullname,fbEmail,accessToken.getToken(),fbGender);
                    nextActivity();
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
        loginButton.setReadPermissions(Arrays.asList("public_profile","email"));
        loginButton.registerCallback(callbackManager, callback);
    }

    private void nextActivity(){
        Log.d("TEST","nextActivity");
        if(profile != null){
            Intent main = new Intent(LoginActivity.this, MainActivity.class);
            main.putExtra("profile",profile);
            startActivity(main);
            finish();
        }
    }

    public class TaskLogin extends AsyncTask<String, Void, Integer> {
        JSONObject jObjResult;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                jObjResult = m_ServiceAccess.convertJSONString2Obj(m_ServiceAccess.getJSONStringWithParam_POST(Constants.ENDPOINT_URL+Constants.AUTH_SERVICE,param));
                if(jObjResult.getBoolean("error")){
                    return Constants.ENDPOINT_ERROR;
                }
                return Constants.ENDPOINT_SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
                return Constants.ENDPOINT_ERROR;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if(result == Constants.ENDPOINT_ERROR){
                Toast.makeText(getApplicationContext(), R.string.service_connection_error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
