package com.app.tesis.eduardo.tesis;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Eduardo on 27/09/2016.
 */

public class MCFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Testimonios", "Fotos", "Audios"};
    private Integer userId;

    public MCFragmentPagerAdapter(FragmentManager fm, Integer userId) {
        super(fm);
        this.userId = userId;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("userId",userId);
        switch (position){
            case 0:
                TestimonyFragment testimonyFragment = new TestimonyFragment();
                testimonyFragment.setArguments(bundle);
                return testimonyFragment;
            case 1:
                PrePhotoFragment prePhotoFragment = new PrePhotoFragment();
                prePhotoFragment.setArguments(bundle);
                return prePhotoFragment;
            case 2:
                PreAudioFragment preAudioFragment = new PreAudioFragment();
                preAudioFragment.setArguments(bundle);
                return preAudioFragment;
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
