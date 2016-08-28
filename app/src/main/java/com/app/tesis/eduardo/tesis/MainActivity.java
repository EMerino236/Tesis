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
    private Profile profile;
    private TextView userFullname;
    private ProfilePictureView profilePicture;
    private Button buttonMaps;
    private Button buttonCamera;
    private Button buttonMyAccount;
    private Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);
        Log.d("TOKEN2", AccessToken.getCurrentAccessToken().getToken());
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
        // Get profile
        inBundle = getIntent().getExtras();
        profile = (Profile) inBundle.get("profile");
        // Get variables ready
        profilePicture = (ProfilePictureView) findViewById(R.id.profile_picture);
        userFullname = (TextView)findViewById(R.id.user_fullname);
        // Set data to variables
        profilePicture.setProfileId(profile.getId());
        //userFullname.setText(profile.getFirstName() + " " + profile.getLastName());
        userFullname.setText(profile.getName());
    }

    public void setMapsButton(){
        buttonMaps = (Button)findViewById(R.id.button_map);
        buttonMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent maps = new Intent(MainActivity.this, MapsActivity.class);
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
                my_account.putExtra("profile",profile);
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
