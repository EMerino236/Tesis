package com.app.tesis.eduardo.tesis;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Eduardo on 26/08/2016.
 */
public class HistoricalEventActivity extends AppCompatActivity {

    private Bundle inBundle;
    private String historicalEventId;
    Integer userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical_event);
        getData();
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new HEFragmentPagerAdapter(getSupportFragmentManager(),HistoricalEventActivity.this,historicalEventId,userId));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void getData(){
        // Get profile
        inBundle = getIntent().getExtras();
        historicalEventId = (String) inBundle.get("historicalEventId");
        userId = (Integer) inBundle.get("userId");
    }
}
