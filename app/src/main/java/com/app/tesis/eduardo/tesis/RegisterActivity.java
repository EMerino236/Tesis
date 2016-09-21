package com.app.tesis.eduardo.tesis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
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
import com.beyondar.android.world.GeoObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eduardo on 1/09/2016.
 */
public class RegisterActivity extends AppCompatActivity {

    EditText name_txt;
    EditText email_txt;
    EditText password_txt;
    EditText repeat_password_txt;
    TextView name_error_lbl;
    TextView email_error_lbl;
    TextView password_error_lbl;
    TextView repeat_password_error_lbl;
    Button register_button;
    Integer userId;
    String fullname;
    String email;
    Boolean post_as_anonymous;
    TextView terms_txt;
    TextView privacy_txt;
    // Webservices
    private AccessServiceAPI m_ServiceAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name_txt = (EditText) findViewById(R.id.name_txt);
        email_txt = (EditText) findViewById(R.id.email_txt);
        password_txt = (EditText) findViewById(R.id.password_txt);
        repeat_password_txt = (EditText) findViewById(R.id.repeat_password_txt);
        name_error_lbl = (TextView) findViewById(R.id.name_error_lbl);
        email_error_lbl = (TextView) findViewById(R.id.email_error_lbl);
        password_error_lbl = (TextView) findViewById(R.id.password_error_lbl);
        repeat_password_error_lbl = (TextView) findViewById(R.id.repeat_password_error_lbl);
        m_ServiceAccess = new AccessServiceAPI();
        setRegisterButton();
        setTextButtons();
    }

    public void setTextButtons(){
        terms_txt = (TextView) findViewById(R.id.terms_txt);
        privacy_txt = (TextView) findViewById(R.id.privacy_txt);
        terms_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent terms = new Intent(RegisterActivity.this, TermsActivity.class);
                startActivity(terms);
            }
        });
        privacy_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent privacy = new Intent(RegisterActivity.this, PrivacyActivity.class);
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
                Intent login = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(login);
                finish();
                return true;
            case R.id.logout_menu_register:
                Toast.makeText(RegisterActivity.this,R.string.menu_same_section,Toast.LENGTH_SHORT).show();
                return true;
            case R.id.logout_menu_recover_password:
                Intent recover = new Intent(RegisterActivity.this, RecoverPassActivity.class);
                startActivity(recover);
                finish();
                return true;
            case R.id.logout_menu_terms:
                Intent terms = new Intent(RegisterActivity.this, TermsActivity.class);
                startActivity(terms);
                finish();
                return true;
            case R.id.logout_menu_privacy:
                Intent privacy = new Intent(RegisterActivity.this, PrivacyActivity.class);
                startActivity(privacy);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setRegisterButton(){
        register_button = (Button) findViewById(R.id.register_button);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate_form()){
                    new TaskRegister().execute(name_txt.getText().toString(),email_txt.getText().toString(),password_txt.getText().toString());
                }
            }
        });
    }

    private boolean validate_form(){
        boolean is_correct = true;
        name_error_lbl.setText(null);
        email_error_lbl.setText(null);
        password_error_lbl.setText(null);
        repeat_password_error_lbl.setText(null);
        if(name_txt.getText().length() == 0){
            name_error_lbl.setText(R.string.required_field);
            is_correct = false;
        }
        if(email_txt.getText().length() == 0){
            email_error_lbl.setText(R.string.required_field);
            is_correct = false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email_txt.getText()).matches()){
            email_error_lbl.setText(R.string.email_patterns_mismatch);
            is_correct = false;
        }
        if(password_txt.getText().length() == 0){
            password_error_lbl.setText(R.string.required_field);
            is_correct = false;
        }
        if(!password_txt.getText().toString().equals(repeat_password_txt.getText().toString())){
            repeat_password_error_lbl.setText(R.string.repeat_password_mismatch);
            is_correct = false;
        }
        return is_correct;
    }

    public class TaskRegister extends AsyncTask<String, Void, Integer> {
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
            param.put("fullname", params[0]);
            param.put("email", params[1]);
            param.put("password", params[2]);

            try {
                jObjResult = m_ServiceAccess.convertJSONString2Obj(m_ServiceAccess.getJSONStringWithParam_POST(Constants.ENDPOINT_URL+Constants.REGISTER,param));
                if(jObjResult.getBoolean("error")){
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
            if(result == Constants.ENDPOINT_ERROR){
                try {
                    String message = jObjResult.getString("message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), R.string.register_error, Toast.LENGTH_LONG).show();
                }
            }else if(result == Constants.ENDPOINT_SUCCESS){
                after_register();
            }
        }
    }

    private void after_register(){
        Intent main = new Intent(RegisterActivity.this, MainActivity.class);
        main.putExtra("login_method","normal");
        main.putExtra("userId",userId);
        main.putExtra("fullname",fullname);
        main.putExtra("email",email);
        main.putExtra("post_as_anonymous",post_as_anonymous);
        startActivity(main);
        finish();
    }
}
