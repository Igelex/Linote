package com.example.android.linote;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * Created by Pastuh on 03.04.2017.
 */

public class SimpleFragmentAdapter extends FragmentPagerAdapter {
    private static final int COUNT = 2;
    public Context mContext;

    public SimpleFragmentAdapter (Context context, FragmentManager fm){
        super(fm);
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.page_title_eng);
            case 1:
                return mContext.getString(R.string.page_title_ger);
            default:
                return null;
        }
    }


    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return  new EngWordListFragment();
            case 1:
                return new GerWordListFragment();
            default:
                Log.e("GerWordListFragment", "Error, cant create Fragment");
                return null;
        }
    }
}
