package com.app.tesis.eduardo.tesis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by Eduardo on 25/08/2016.
 */
public class PrivacyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
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
                Intent login = new Intent(PrivacyActivity.this, LoginActivity.class);
                startActivity(login);
                finish();
                return true;
            case R.id.logout_menu_register:
                Intent register = new Intent(PrivacyActivity.this, RegisterActivity.class);
                startActivity(register);
                finish();
                return true;
            case R.id.logout_menu_recover_password:
                Intent recover = new Intent(PrivacyActivity.this, RecoverPassActivity.class);
                startActivity(recover);
                finish();
                return true;
            case R.id.logout_menu_terms:
                Intent terms = new Intent(PrivacyActivity.this, TermsActivity.class);
                startActivity(terms);
                finish();
                return true;
            case R.id.logout_menu_privacy:
                Toast.makeText(PrivacyActivity.this,R.string.menu_same_section,Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
