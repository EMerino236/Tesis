package com.app.tesis.eduardo.tesis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;

public class MainActivity extends AppCompatActivity {

    private Integer citizenId;
    private Bundle inBundle;
    //private Profile profile;
    private TextView userFullname;
    private ProfilePictureView profilePicture;
    private Button buttonMaps;
    private Button buttonCamera;
    private Button buttonMyAccount;
    private Button buttonLogout;
    Integer userId;
    String fbId;
    String fbFullname;
    String fbEmail;
    String login_method;
    Boolean post_as_anonymous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);
        //Log.d("TOKEN2", AccessToken.getCurrentAccessToken().getToken());
        // Set profile data from login
        setProfileData();
        // Set maps button
        setMapsButton();
        // Set camera button
        setCameraButton();
        // Set my account button
        setMyAccountButton();
        // Set logout button
        setLogoutButton();
    }

    public void setProfileData(){
        // Get variables ready
        profilePicture = (ProfilePictureView) findViewById(R.id.profile_picture);
        userFullname = (TextView)findViewById(R.id.user_fullname);
        // Get profile
        inBundle = getIntent().getExtras();
        //profile = (Profile) inBundle.get("profile");
        login_method = (String) inBundle.get("login_method");
        if(login_method.equals("fb")){
            fbId = (String) inBundle.get("fbId");
            profilePicture.setProfileId(fbId);
        }
        userId = (Integer) inBundle.get("userId");
        fbFullname = (String) inBundle.get("fullname");
        fbEmail = (String) inBundle.get("email");
        post_as_anonymous = (Boolean) inBundle.get("post_as_anonymous");
        userFullname.setText(fbFullname);
    }

    public void setMapsButton(){
        buttonMaps = (Button)findViewById(R.id.button_map);
        buttonMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent maps = new Intent(MainActivity.this, MapsActivity.class);
                maps.putExtra("login_method",login_method);
                if(login_method.equals("fb")) {
                    maps.putExtra("fbId", fbId);
                }
                maps.putExtra("userId",userId);
                maps.putExtra("fullname",fbFullname);
                maps.putExtra("email",fbEmail);
                maps.putExtra("post_as_anonymous",post_as_anonymous);
                startActivity(maps);
            }
        });
    }

    public void setCameraButton(){
        buttonCamera = (Button)findViewById(R.id.button_camera);
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera = new Intent(MainActivity.this, CameraActivity.class);
                camera.putExtra("login_method",login_method);
                if(login_method.equals("fb")) {
                    camera.putExtra("fbId", fbId);
                }
                camera.putExtra("userId",userId);
                camera.putExtra("fullname",fbFullname);
                camera.putExtra("email",fbEmail);
                camera.putExtra("post_as_anonymous",post_as_anonymous);
                startActivity(camera);
            }
        });
    }

    public void setMyAccountButton(){
        buttonMyAccount = (Button)findViewById(R.id.button_my_account);
        buttonMyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent my_account = new Intent(MainActivity.this, MyAccountActivity.class);
                //my_account.putExtra("profile",profile);
                my_account.putExtra("login_method",login_method);
                if(login_method.equals("fb")) {
                    my_account.putExtra("fbId", fbId);
                }
                my_account.putExtra("userId",userId);
                my_account.putExtra("fullname",fbFullname);
                my_account.putExtra("email",fbEmail);
                my_account.putExtra("post_as_anonymous",post_as_anonymous);
                startActivity(my_account);
            }
        });
    }

    public void setLogoutButton(){
        buttonLogout = (Button)findViewById(R.id.button_logout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                Intent login = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(login);
                finish();
            }
        });
    }
}
