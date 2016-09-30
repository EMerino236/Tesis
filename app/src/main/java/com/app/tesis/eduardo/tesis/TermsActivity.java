package com.app.tesis.eduardo.tesis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import static com.app.tesis.eduardo.tesis.utils.CustomToast.centeredToast;

/**
 * Created by Eduardo on 25/08/2016.
 */
public class TermsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
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
                Intent login = new Intent(TermsActivity.this, LoginActivity.class);
                startActivity(login);
                finish();
                return true;
            case R.id.logout_menu_register:
                Intent register = new Intent(TermsActivity.this, RegisterActivity.class);
                startActivity(register);
                finish();
                return true;
            case R.id.logout_menu_recover_password:
                Intent recover = new Intent(TermsActivity.this, RecoverPassActivity.class);
                startActivity(recover);
                finish();
                return true;
            case R.id.logout_menu_terms:
                centeredToast(TermsActivity.this,getString(R.string.menu_same_section));
                //Toast.makeText(TermsActivity.this,R.string.menu_same_section,Toast.LENGTH_SHORT).show();
                return true;
            case R.id.logout_menu_privacy:
                Intent privacy = new Intent(TermsActivity.this, PrivacyActivity.class);
                startActivity(privacy);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
