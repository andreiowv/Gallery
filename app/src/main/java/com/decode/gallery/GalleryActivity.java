package com.decode.gallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class GalleryActivity extends AppCompatActivity implements ICallback{
    private ViewPager mPager;
    private TabLayout mTabs;
    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private NavigationView mNavigation;
    private FloatingActionButton mFAB;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);


        mPager= (ViewPager) findViewById(R.id.pager);
        mTabs = findViewById(R.id.tabs);
        mDrawer = findViewById(R.id.drawer_layout);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

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
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                String ret = "Photos";
                switch (position){
                    case 0 : ret = "Photos";
                    break;
                    case 1: ret = "Videos";
                    break;
                }
                return ret;
            }

        });


        mTabs.setupWithViewPager(mPager);

        mNavigation = findViewById(R.id.drawer_navigation);
        mNavigation.setCheckedItem(R.id.action_photos);
        mNavigation.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawer.closeDrawers();
                        if(menuItem.getItemId() == R.id.action_photos){
                            mPager.setCurrentItem(0);
                        } else if (menuItem.getItemId() == R.id.action_videos) {
                            mPager.setCurrentItem(1);
                        }

                        return true;
                    }
                });

        mFAB = findViewById(R.id.fab);
        mFAB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent,2);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1)
            mPager.setCurrentItem(resultCode);
        else if (requestCode == 2) {
            Snackbar.make((View) findViewById(R.id.my_FabParent), "Here's a Snackbar", Snackbar.LENGTH_LONG)
                       .setAction("Action", new MyToast(getApplicationContext())).show();
        } else if (requestCode == 555) {
            for (Fragment f : getSupportFragmentManager().getFragments())
                f.onActivityResult(requestCode, resultCode, data);
        }

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
    public void preview(Media media) {
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra("color", "");
        startActivityForResult(intent, 1);
    }

    @Override
    public void showPermissions() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 4);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gallery_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_photos) {
            mPager.setCurrentItem(0);
        } else if (item.getItemId() == R.id.action_videos) {
            mPager.setCurrentItem(1);
        } else if (item.getItemId() == android.R.id.home) {
            mDrawer.openDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 555) {
            Permissions.reset();
            for (Fragment f : getSupportFragmentManager().getFragments())
                f.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Permissions.reset();
    }
}
