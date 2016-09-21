package com.app.tesis.eduardo.tesis;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.tesis.eduardo.tesis.services.AccessServiceAPI;

/**
 * Created by Eduardo on 20/09/2016.
 */

public class TestimonyActivity extends AppCompatActivity {

    EditText title_txt;
    EditText review_txt;
    EditText day_txt;
    EditText month_txt;
    EditText year_txt;
    Spinner period_txt;
    TextView title_error_lbl;
    TextView review_error_lbl;
    TextView day_error_lbl;
    TextView month_error_lbl;
    TextView year_error_lbl;
    TextView period_error_lbl;
    Button add_button;
    Bundle inBundle;
    Long pointId;
    Integer userId;
    String fbId;
    String fbFullname;
    String fbEmail;
    String login_method;
    Boolean post_as_anonymous;
    // Webservices
    private AccessServiceAPI m_ServiceAccess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testimony);
        setUISettings();
        setProfileData();
    }

    public void setUISettings(){
        title_txt = (EditText) findViewById(R.id.title_txt);
        day_txt = (EditText) findViewById(R.id.day_txt);
        month_txt = (EditText) findViewById(R.id.month_txt);
        year_txt = (EditText) findViewById(R.id.year_txt);
        period_txt = (Spinner) findViewById(R.id.period_txt);
        title_error_lbl = (TextView) findViewById(R.id.title_error_lbl);
        review_error_lbl = (TextView) findViewById(R.id.review_error_lbl);
        day_error_lbl = (TextView) findViewById(R.id.day_error_lbl);
        month_error_lbl = (TextView) findViewById(R.id.month_error_lbl);
        year_error_lbl = (TextView) findViewById(R.id.year_error_lbl);
        period_error_lbl = (TextView) findViewById(R.id.period_error_lbl);
        add_button = (Button) findViewById(R.id.add_button);
        // Initialize the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.periods_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        period_txt.setAdapter(adapter);
    }

    public void setProfileData(){
        // Get profile
        inBundle = getIntent().getExtras();
        pointId = (Long) inBundle.get("pointId");
        login_method = (String) inBundle.get("login_method");
        if(login_method.equals("fb")){
            fbId = (String) inBundle.get("fbId");
        }
        userId = (Integer) inBundle.get("userId");
        fbFullname = (String) inBundle.get("fullname");
        fbEmail = (String) inBundle.get("email");
        post_as_anonymous = (Boolean) inBundle.get("post_as_anonymous");
    }
}
