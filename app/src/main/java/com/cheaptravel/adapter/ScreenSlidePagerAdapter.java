package com.cheaptravel.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class ScreenSlidePagerAdapter   extends FragmentPagerAdapter {

    private final Context mContext;
    private List<Fragment> fragments ;

    public ScreenSlidePagerAdapter(Context context, FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mContext = context;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {

        if(fragments == null || fragments.isEmpty()){
            return null;
        }
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}

