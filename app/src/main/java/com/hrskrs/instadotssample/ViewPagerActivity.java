package com.hrskrs.instadotssample;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hrskrs.instadotlib.InstaDotView;

public class ViewPagerActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    InstaDotView instaDotView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);

        ViewPager viewPager = findViewById(R.id.viewpager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        for (int x=0; x<20; x++) {
            viewPagerAdapter.addFragment(ViewPagerFragment.instance(x+1), "Page "+(x+1));
        }
        viewPager.setAdapter(viewPagerAdapter);

        instaDotView = findViewById(R.id.instadot);
        instaDotView.setupViewPager(viewPager);
        instaDotView.addOnDotPageListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d("Test", position+"");
        instaDotView.onPageChange(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
