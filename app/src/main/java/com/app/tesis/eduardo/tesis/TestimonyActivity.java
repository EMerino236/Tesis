package com.app.tesis.eduardo.tesis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.tesis.eduardo.tesis.services.AccessServiceAPI;
import com.app.tesis.eduardo.tesis.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.app.tesis.eduardo.tesis.utils.CustomToast.centeredToast;

/**
 * Created by Eduardo on 20/09/2016.
 */

public class TestimonyActivity extends AppCompatActivity {

    EditText title_txt;
    EditText description_txt;
    EditText day_txt;
    Spinner month_txt;
    EditText year_txt;
    Spinner period_txt;
    TextView title_error_lbl;
    TextView description_error_lbl;
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
    ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testimony);
        setWebservicesSettings();
        setUISettings();
        setProfileData();
        setAddButton();
    }

    public void setWebservicesSettings(){
        m_ServiceAccess = new AccessServiceAPI();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.progress_dialog_points_message));
        mProgressDialog.setTitle(R.string.progress_dialog_title);
    }

    public void setUISettings(){
        title_txt = (EditText) findViewById(R.id.title_txt);
        description_txt = (EditText) findViewById(R.id.description_txt);
        day_txt = (EditText) findViewById(R.id.day_txt);
        month_txt = (Spinner) findViewById(R.id.month_txt);
        year_txt = (EditText) findViewById(R.id.year_txt);
        period_txt = (Spinner) findViewById(R.id.period_txt);
        title_error_lbl = (TextView) findViewById(R.id.title_error_lbl);
        description_error_lbl = (TextView) findViewById(R.id.description_error_lbl);
        day_error_lbl = (TextView) findViewById(R.id.day_error_lbl);
        month_error_lbl = (TextView) findViewById(R.id.month_error_lbl);
        year_error_lbl = (TextView) findViewById(R.id.year_error_lbl);
        period_error_lbl = (TextView) findViewById(R.id.period_error_lbl);
        // Initialize the month spinner
        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(this,R.array.months_array, android.R.layout.simple_spinner_dropdown_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        month_txt.setAdapter(monthAdapter);
        // Initialize the period spinner
        ArrayAdapter<CharSequence> PeriodAdapter = ArrayAdapter.createFromResource(this,R.array.periods_array, android.R.layout.simple_spinner_item);
        PeriodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        period_txt.setAdapter(PeriodAdapter);
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

    public void setAddButton(){
        add_button = (Button) findViewById(R.id.add_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate_form()){
                    new TaskAdd(mProgressDialog,TestimonyActivity.this).execute(String.valueOf(pointId),String.valueOf(userId),title_txt.getText().toString(),description_txt.getText().toString(),day_txt.getText().toString(),String.valueOf(month_txt.getSelectedItemPosition()),year_txt.getText().toString(),period_txt.getSelectedItem().toString());
                }
            }
        });
    }

    private boolean validate_form(){
        boolean is_correct = true;
        title_error_lbl.setText(null);
        description_error_lbl.setText(null);
        day_error_lbl.setText(null);
        month_error_lbl.setText(null);
        year_error_lbl.setText(null);
        period_error_lbl.setText(null);
        if(title_txt.getText().length() == 0){
            title_error_lbl.setText(R.string.required_field);
            is_correct = false;
        }
        if(description_txt.getText().length() == 0){
            description_error_lbl.setText(R.string.required_field);
            is_correct = false;
        }
        if(year_txt.getText().length() == 0){
            year_error_lbl.setText(R.string.required_field);
            is_correct = false;
        }
        if(period_txt.getSelectedItem() == null){
            period_error_lbl.setText(R.string.required_field);
            is_correct = false;
        }
        if(day_txt.getText().length() > 0 && month_txt.getSelectedItemPosition() == 0){
            day_error_lbl.setText(R.string.no_month_selected);
            is_correct = false;
        }
        if(day_txt.getText().length() > 0 && !isInteger(day_txt.getText().toString())){
            day_error_lbl.setText(R.string.number_required);
            is_correct = false;
        }
        if(day_txt.getText().length() > 0 && isInteger(day_txt.getText().toString()) && Integer.parseInt( day_txt.getText().toString() )>31){
            day_error_lbl.setText(R.string.day_out_of_range);
            is_correct = false;
        }
        return is_correct;
    }

    public boolean isInteger( String input )
    {
        try{
            Integer.parseInt( input );
            return true;
        }catch( Exception e){
            return false;
        }
    }

    public class TaskAdd extends AsyncTask<String, Void, Integer> {
        JSONObject jObjResult;
        ProgressDialog progressDialog;
        TestimonyActivity activity;
        public TaskAdd(ProgressDialog mProgressDialog, TestimonyActivity act){
            this.progressDialog = mProgressDialog;
            this.activity = act;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            add_button.setEnabled(false);
        }

        @Override
        protected Integer doInBackground(String... params) {
            //Create data to pass in param
            Map<String, String> param = new HashMap<>();
            param.put("point_id", params[0]);
            param.put("citizen_id", params[1]);
            param.put("title", params[2]);
            param.put("description", params[3]);
            param.put("day", params[4]);
            param.put("month", params[5]);
            param.put("year", params[6]);
            param.put("period", params[7]);

            try {
                jObjResult = m_ServiceAccess.convertJSONString2Obj(m_ServiceAccess.getJSONStringWithParam_POST(Constants.ENDPOINT_URL+Constants.ADD_HISTORICAL_EVENT,param));
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
            progressDialog.dismiss();
            if(result == Constants.ENDPOINT_ERROR){
                try {
                    String message = jObjResult.getString("message");
                    centeredToast(getApplicationContext(),message);
                    //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    centeredToast(getApplicationContext(),getString(R.string.add_error));
                    //Toast.makeText(getApplicationContext(), R.string.add_error, Toast.LENGTH_LONG).show();
                }
            }else if(result == Constants.ENDPOINT_SUCCESS){
                centeredToast(getApplicationContext(),getString(R.string.add_success));
                activity.finish();
                //Toast.makeText(getApplicationContext(), R.string.add_success, Toast.LENGTH_LONG).show();
            }
            add_button.setEnabled(true);
        }
    }
}
