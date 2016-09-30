package com.app.tesis.eduardo.tesis;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Eduardo on 27/09/2016.
 */

public class MyContributionsActivity extends AppCompatActivity {

    private Bundle inBundle;
    Integer userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contributions);
        getData();
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MCFragmentPagerAdapter(getSupportFragmentManager(),userId));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void getData(){
        // Get profile
        inBundle = getIntent().getExtras();
        userId = (Integer) inBundle.get("userId");
    }
}
