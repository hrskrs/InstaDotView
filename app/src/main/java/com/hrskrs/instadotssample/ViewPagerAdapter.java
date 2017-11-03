package com.hrskrs.instadotssample;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : hafiq on 01/09/2017.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragmentList;
    private List<String> mFragmentTitleList;

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
        mFragmentTitleList = new ArrayList<>();
        mFragmentList = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int index) {
        return mFragmentList.get(index);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }

    public void addFragment(Fragment fragment,String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    public void deletePage(int position){
        mFragmentList.remove(position);
        mFragmentTitleList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}