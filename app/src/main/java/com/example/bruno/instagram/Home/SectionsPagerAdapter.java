package com.example.bruno.instagram.Home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bruno on 20/04/2018.
 */

/**
 * Clase que adapta los frgamentos a las diferentes pestañas
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "SectionsPagerAdapter";

    private  final List<Fragment> mfFragmentList = new ArrayList<>();


    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mfFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mfFragmentList.size();
    }

    public void addFragment(Fragment fragment){
        mfFragmentList.add(fragment);
    }
}
