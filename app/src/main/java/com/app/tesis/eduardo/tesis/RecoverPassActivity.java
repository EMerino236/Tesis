package com.app.tesis.eduardo.tesis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eduardo on 1/09/2016.
 */
public class RecoverPassActivity extends AppCompatActivity {

    EditText email_txt;
    EditText repeat_email_txt;
    TextView email_error_lbl;
    TextView repeat_email_error_lbl;
    Button recover_button;
    // Webservices
    private AccessServiceAPI m_ServiceAccess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_pass);
        email_txt = (EditText) findViewById(R.id.email_txt);
        repeat_email_txt = (EditText) findViewById(R.id.repeat_email_txt);
        email_error_lbl = (TextView) findViewById(R.id.email_error_lbl);
        repeat_email_error_lbl = (TextView) findViewById(R.id.repeat_email_error_lbl);
        recover_button = (Button) findViewById(R.id.recover_button);
        m_ServiceAccess = new AccessServiceAPI();
        setRecoverButton();
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
                Intent login = new Intent(RecoverPassActivity.this, LoginActivity.class);
                startActivity(login);
                finish();
                return true;
            case R.id.logout_menu_register:
                Intent register = new Intent(RecoverPassActivity.this, RegisterActivity.class);
                startActivity(register);
                finish();
                return true;
            case R.id.logout_menu_recover_password:
                Toast.makeText(RecoverPassActivity.this,R.string.menu_same_section,Toast.LENGTH_SHORT).show();
                return true;
            case R.id.logout_menu_terms:
                Intent terms = new Intent(RecoverPassActivity.this, TermsActivity.class);
                startActivity(terms);
                finish();
                return true;
            case R.id.logout_menu_privacy:
                Intent privacy = new Intent(RecoverPassActivity.this, PrivacyActivity.class);
                startActivity(privacy);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setRecoverButton(){
        recover_button = (Button) findViewById(R.id.recover_button);
        recover_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate_form()){
                    new TaskRecover().execute(email_txt.getText().toString());
                }
            }
        });
    }

    private boolean validate_form(){
        boolean is_correct = true;
        email_error_lbl.setText(null);
        repeat_email_error_lbl.setText(null);
        if(email_txt.getText().length() == 0){
            email_error_lbl.setText(R.string.required_field);
            is_correct = false;
        }
        if(!email_txt.getText().toString().equals(repeat_email_txt.getText().toString())){
            repeat_email_error_lbl.setText(R.string.repeat_email_mismatch);
            is_correct = false;
        }
        return is_correct;
    }

    public class TaskRecover extends AsyncTask<String, Void, Integer> {
        JSONObject jObjResult;
        private ProgressDialog mProgressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            //Create data to pass in param
            Map<String, String> param = new HashMap<>();
            param.put("email", params[0]);

            try {
                jObjResult = m_ServiceAccess.convertJSONString2Obj(m_ServiceAccess.getJSONStringWithParam_POST(Constants.ENDPOINT_URL+Constants.RECOVER,param));
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
                Toast.makeText(getApplicationContext(), R.string.recover_error, Toast.LENGTH_LONG).show();
            }else if(result == Constants.ENDPOINT_SUCCESS){
                Toast.makeText(getApplicationContext(), R.string.recover_success, Toast.LENGTH_LONG).show();
            }
        }
    }
}
