package com.app.tesis.eduardo.tesis;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;

/**
 * Created by Eduardo on 14/07/2016.
 */
public class MyAccountActivity extends AppCompatActivity {

    private Bundle inBundle;
    private Profile profile;
    private TextView userFullname;
    private TextView email;
    private ProfilePictureView profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_my_account);
        // Set profile data from login
        setProfileData();
    }

    public void setProfileData(){
        // Get profile
        inBundle = getIntent().getExtras();
        profile = (Profile) inBundle.get("profile");
        // Get variables ready
        profilePicture = (ProfilePictureView) findViewById(R.id.profile_picture);
        userFullname = (TextView)findViewById(R.id.user_fullname);
        email = (TextView) findViewById(R.id.email);
        // Set data to variables
        profilePicture.setProfileId(profile.getId());
        userFullname.setText(profile.getFirstName() + " " + profile.getLastName());
    }
}
