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
import android.widget.EditText;
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
    private Profile profile;
    private TextView userFullname;
    private TextView email;
    private ProfilePictureView profilePicture;
    private Switch buttonSwitch;
    private Button buttonSave;
    private Boolean switchValue;
    Integer userId;
    String fbId;
    String fbFullname;
    String fbEmail;
    String login_method;
    Boolean post_as_anonymous;
    EditText name_txt;
    EditText password_txt;
    EditText repeat_password_txt;
    TextView name_error_lbl;
    TextView password_error_lbl;
    TextView repeat_password_error_lbl;
    // Webservices
    private AccessServiceAPI m_ServiceAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_my_account);
        name_txt = (EditText) findViewById(R.id.name_txt);
        password_txt = (EditText) findViewById(R.id.password_txt);
        repeat_password_txt = (EditText) findViewById(R.id.repeat_password_txt);
        name_error_lbl = (TextView) findViewById(R.id.name_error_lbl);
        password_error_lbl = (TextView) findViewById(R.id.password_error_lbl);
        repeat_password_error_lbl = (TextView) findViewById(R.id.repeat_password_error_lbl);
        m_ServiceAccess = new AccessServiceAPI();
        // Set profile data from facebook login
        setProfileData();
        // Fetch citizen data fron service
        //fetchCitizenData();
        // Set save button
        setSaveButton();
    }

    public void setProfileData(){
        // Get variables ready
        profilePicture = (ProfilePictureView) findViewById(R.id.profile_picture);
        buttonSwitch = (Switch) findViewById(R.id.post_as_anonymous);
        buttonSave = (Button) findViewById(R.id.button_save);
        email = (TextView) findViewById(R.id.email);
        // Get profile
        inBundle = getIntent().getExtras();
        profile = (Profile) inBundle.get("profile");
        login_method = (String) inBundle.get("login_method");
        if(login_method.equals("fb")){
            fbId = (String) inBundle.get("fbId");
            profilePicture.setProfileId(fbId);
        }
        userId = (Integer) inBundle.get("userId");
        fbFullname = (String) inBundle.get("fullname");
        fbEmail = (String) inBundle.get("email");
        post_as_anonymous = (Boolean) inBundle.get("post_as_anonymous");
        name_txt.setText(fbFullname);
        email.setText(fbEmail);
        if(post_as_anonymous){
            buttonSwitch.setChecked(true);
            buttonSwitch.setText(R.string.label_yes);
        }else{
            buttonSwitch.setChecked(false);
            buttonSwitch.setText(R.string.label_no);
        }
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
    }
/*
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
*/
    public void setSaveButton(){
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate_form()){
                    new TaskSaveMyAccount().execute(String.valueOf(userId),name_txt.getText().toString(),password_txt.getText().toString(),String.valueOf(buttonSwitch.isChecked()));
                }
                /*
                // Save citizen service
                if(buttonSwitch.isChecked()){
                    switchValue = true;
                }else{
                    switchValue = false;
                }
                new TaskSaveMyAccount().execute(String.valueOf(userId),String.valueOf(buttonSwitch.isChecked()));
                */
            }
        });
    }

    private boolean validate_form(){
        boolean is_correct = true;
        name_error_lbl.setText(null);
        password_error_lbl.setText(null);
        repeat_password_error_lbl.setText(null);
        if(name_txt.getText().length() == 0){
            name_error_lbl.setText(R.string.required_field);
            is_correct = false;
        }
        if((password_txt.getText().length() > 0) && !password_txt.getText().toString().equals(repeat_password_txt.getText().toString())){
            repeat_password_error_lbl.setText(R.string.repeat_password_mismatch);
            is_correct = false;
        }
        return is_correct;
    }
/*
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
*/
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
            param.put("fullname", params[1]);
            param.put("password", params[2]);
            param.put("post_as_anonymous", params[3]);
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
                    name_txt.setText(jObjResult.getJSONObject("citizen").getString("fullname"));
                    if(jObjResult.getJSONObject("citizen").getBoolean("post_as_anonymous")){
                        buttonSwitch.setChecked(true);
                        buttonSwitch.setText(R.string.label_yes);
                    }else{
                        buttonSwitch.setChecked(false);
                        buttonSwitch.setText(R.string.label_no);
                    }
                    password_txt.setText(null);
                    repeat_password_txt.setText(null);
                    Toast.makeText(getApplicationContext(), R.string.my_account_edit_success, Toast.LENGTH_SHORT).show();
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
