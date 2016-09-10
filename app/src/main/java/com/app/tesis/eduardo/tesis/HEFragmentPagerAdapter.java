package com.app.tesis.eduardo.tesis;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Eduardo on 8/09/2016.
 */
public class HEFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 4;
    private String tabTitles[] = new String[] { "Info", "Fotos", "Audios", "Aportar"};
    private Context context;
    private String historicalEventId;

    public HEFragmentPagerAdapter(FragmentManager fm, Context context,String historicalEventId) {
        super(fm);
        this.context = context;
        this.historicalEventId = historicalEventId;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("historicalEventId",historicalEventId);
        switch (position){
            case 0:
                InfoFragment infoFragment = new InfoFragment();
                infoFragment.setArguments(bundle);
                return infoFragment;
            case 1:
                PhotoFragment photoFragment = new PhotoFragment();
                photoFragment.setArguments(bundle);
                return photoFragment;
            case 2:
                AudioFragment audioFragment = new AudioFragment();
                audioFragment.setArguments(bundle);
                return audioFragment;
            case 3:
                UploadFragment uploadFragment = new UploadFragment();
                uploadFragment.setArguments(bundle);
                return uploadFragment;
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
