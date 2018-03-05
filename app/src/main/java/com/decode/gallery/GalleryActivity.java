package com.decode.gallery;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GalleryActivity extends AppCompatActivity implements ICallback{
    private ViewPager mPager;
    private TabLayout mTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);


        mPager= (ViewPager) findViewById(R.id.pager);
        mTabs = findViewById(R.id.tabs);

        mPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                GalleryFragment fragment = new GalleryFragment();
                Bundle args = new Bundle();
                args.putInt("type", position);
                fragment.setArguments(args);
                return  fragment;
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "Page " + position;
            }

        });


        mTabs.setupWithViewPager(mPager);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPager.setCurrentItem(resultCode);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putInt("type", mPager.getCurrentItem());

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
            mPager.setCurrentItem(savedInstanceState.getInt("type"));
    }

    @Override
    public void preview(int type) {
        Intent intent = new Intent(this, PreviewActivity.class);
        startActivityForResult(intent, 1);
    }
}
