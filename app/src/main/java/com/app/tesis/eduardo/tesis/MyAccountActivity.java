package com.app.tesis.eduardo.tesis;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.app.tesis.eduardo.tesis.services.AccessServiceAPI;
import com.app.tesis.eduardo.tesis.utils.Constants;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eduardo on 14/07/2016.
 */
public class MyAccountActivity extends AppCompatActivity {

    private Bundle inBundle;
    private Integer userId;
    private Profile profile;
    private TextView userFullname;
    private TextView email;
    private ProfilePictureView profilePicture;
    private Switch buttonSwitch;
    private Button buttonSave;
    private Boolean switchValue;
    // Webservices
    private AccessServiceAPI m_ServiceAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_my_account);
        m_ServiceAccess = new AccessServiceAPI();
        // Set profile data from facebook login
        setProfileData();
        // Fetch citizen data fron service
        fetchCitizenData();
        // Set save button
        setSaveButton();
    }

    public void setProfileData(){
        // Get profile
        inBundle = getIntent().getExtras();
        profile = (Profile) inBundle.get("profile");
        // Get variables ready
        profilePicture = (ProfilePictureView) findViewById(R.id.profile_picture);
        userFullname = (TextView)findViewById(R.id.user_fullname);
        email = (TextView) findViewById(R.id.email);
        buttonSwitch = (Switch) findViewById(R.id.post_as_anonymous);
        buttonSave = (Button) findViewById(R.id.button_save);
        // Set data to variables
        profilePicture.setProfileId(profile.getId());
        userFullname.setText(profile.getName());
    }

    public void fetchCitizenData(){
        //attach a listener to check for changes in state
        buttonSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getApplicationContext(),R.string.switch_text_on,Toast.LENGTH_SHORT).show();
                    buttonSwitch.setText(R.string.label_yes);
                }else{
                    Toast.makeText(getApplicationContext(),R.string.switch_text_off,Toast.LENGTH_SHORT).show();
                    buttonSwitch.setText(R.string.label_no);
                }
            }
        });
        // Set profile data from service
        new TaskMyAccount().execute(profile.getId(), AccessToken.getCurrentAccessToken().getToken());
    }

    public void setSaveButton(){
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save citizen service
                if(buttonSwitch.isChecked()){
                    switchValue = true;
                }else{
                    switchValue = false;
                }
                new TaskSaveMyAccount().execute(String.valueOf(userId),String.valueOf(buttonSwitch.isChecked()));
            }
        });
    }

    public class TaskMyAccount extends AsyncTask<String, Void, Integer> {
        JSONObject jObjResult;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            //Create data to pass in param
            Log.d("MA","MY ACCOUNT");
            try {
                jObjResult = m_ServiceAccess.convertJSONString2Obj(m_ServiceAccess.getJSONStringFromUrl_GET(Constants.ENDPOINT_URL+Constants.CITIZENS+params[0]));
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
            if(result == Constants.ENDPOINT_SUCCESS){
                try {
                    email.setText(jObjResult.getJSONObject("citizen").getString("email"));
                    Log.d("TEST", String.valueOf(jObjResult.getJSONObject("citizen").getBoolean("post_as_anonymous")));
                    userId = jObjResult.getJSONObject("citizen").getInt("id");
                    if(jObjResult.getJSONObject("citizen").getBoolean("post_as_anonymous")){
                        buttonSwitch.setChecked(true);
                        buttonSwitch.setText(R.string.label_yes);
                    }else{
                        buttonSwitch.setChecked(false);
                        buttonSwitch.setText(R.string.label_no);
                    }
                    buttonSave.setEnabled(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), R.string.service_connection_error, Toast.LENGTH_SHORT).show();
                }
            }else if(result == Constants.ENDPOINT_ERROR){
                Toast.makeText(getApplicationContext(), R.string.service_connection_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class TaskSaveMyAccount extends AsyncTask<String, Void, Integer> {
        JSONObject jObjResult;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            buttonSave.setEnabled(false);
        }

        @Override
        protected Integer doInBackground(String... params) {
            //Create data to pass in param
            Map<String, String> param = new HashMap<>();
            param.put("post_as_anonymous", params[1]);
            Log.d("MA","MY ACCOUNT");
            try {
                jObjResult = m_ServiceAccess.convertJSONString2Obj(m_ServiceAccess.getJSONStringWithParam_POST(Constants.ENDPOINT_URL+Constants.CITIZENS+params[0],param));
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
            if(result == Constants.ENDPOINT_SUCCESS){
                try {
                    if(jObjResult.getJSONObject("citizen").getBoolean("post_as_anonymous")){
                        buttonSwitch.setChecked(true);
                        buttonSwitch.setText(R.string.label_yes);
                    }else{
                        buttonSwitch.setChecked(false);
                        buttonSwitch.setText(R.string.label_no);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), R.string.service_connection_error, Toast.LENGTH_SHORT).show();
                }
            }else if(result == Constants.ENDPOINT_ERROR){
                Toast.makeText(getApplicationContext(), R.string.service_connection_error, Toast.LENGTH_SHORT).show();
            }
            buttonSave.setEnabled(true);
        }
    }
}
