package com.decode.gallery;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
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
import android.view.Window;

import com.crashlytics.android.Crashlytics;

import java.util.HashMap;

import io.fabric.sdk.android.Fabric;


public class GalleryActivity extends AppCompatActivity implements ICallback{
    private ViewPager mPager;
    private TabLayout mTabs;
    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private NavigationView mNavigation;
    private FloatingActionButton mFAB;
    private HashMap<String, Integer> mVisits;
    private DB.Helper mDB;
    private Cloud mCloud;
    private boolean mBound = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDB = new DB.Helper(this);

        if(savedInstanceState != null) {
            mVisits = (HashMap<String, Integer>) savedInstanceState.getSerializable("mVisits");
        } else {
            mVisits = new HashMap<String, Integer>();
            SQLiteDatabase db = mDB.getReadableDatabase();

            Cursor cursor = db.rawQuery("SELECT * FROM " + DB.Visit.Entry.TABLE_NAME, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    mVisits.put(cursor.getString(cursor.getColumnIndex(DB.Visit.Entry.COLUMN_URL)), cursor.getInt(cursor.getColumnIndex(DB.Visit.Entry.COLUMN_VISITS)));
                } while (cursor.moveToNext());
            }

        }

        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
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
                if(position == 3){
                    CloudGalleryFragment cloudFragment = new CloudGalleryFragment();
                    Bundle args = new Bundle();
                    args.putInt("type", position);
                    cloudFragment.setArguments(args);
                    return cloudFragment;
                } if (position == 2){
                    SoundGalleryFragment cloudFragment = new SoundGalleryFragment();
                    Bundle args = new Bundle();
                    args.putInt("type", position);
                    cloudFragment.setArguments(args);
                    return cloudFragment;
                } else {
                    GalleryFragment fragment = new GalleryFragment();
                    Bundle args = new Bundle();
                    args.putInt("type", position);
                    fragment.setArguments(args);
                    return fragment;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                String ret = "Photos";
                switch (position){
                    case 0 : ret = "Photos";
                    break;
                    case 1: ret = "Videos";
                    break;
                    case 2: ret = "Sounds";
                    break;
                    case 3: ret = "Cloud";
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
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            if ( resultCode == RESULT_OK) {
                Media media = data.getParcelableExtra("media");
                int v = mVisits.containsKey(media.getUrl()) ? mVisits.get(media.getUrl()) : 0;
                mVisits.put(media.getUrl(), v + 1);
                for (Fragment f : getSupportFragmentManager().getFragments())
                    f.onActivityResult(requestCode, resultCode, data);
            }
        } else if (requestCode == 2) {
            Snackbar.make((View) findViewById(R.id.my_FabParent), "Here's a Snackbar", Snackbar.LENGTH_LONG)
                       .setAction("Action", new MyToast(getApplicationContext())).show();
        } else if (requestCode == 555) {
            for (Fragment f : getSupportFragmentManager().getFragments())
                f.onActivityResult(requestCode, resultCode, data);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, Cloud.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putInt("type", mPager.getCurrentItem());
        outState.putSerializable("mVisits", mVisits);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
            mPager.setCurrentItem(savedInstanceState.getInt("type"));
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void preview(Media media, View view) {
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra("media", media);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, view.findViewById(R.id.thumb), "thumbnail");
        startActivityForResult(intent, 1, options.toBundle());
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

    @Override
    public int getVisits(Media media) {
        return mVisits.containsKey(media.getUrl()) ? mVisits.get(media.getUrl()) : 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        SQLiteDatabase db = mDB.getReadableDatabase();
        for(String key : mVisits.keySet()) {
            ContentValues values = new ContentValues();
            values.put(DB.Visit.Entry.COLUMN_URL, key);
            values.put(DB.Visit.Entry.COLUMN_VISITS, mVisits.get(key));

            if (db.update(DB.Visit.Entry.TABLE_NAME, values,
                    DB.Visit.Entry.COLUMN_URL + "= ?", new String[]{key}) <= 0)
                db.insert(DB.Visit.Entry.TABLE_NAME, null, values);

        }
//        SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
//        prefs.edit().putString("visits", new Gson().toJson(mVisits)).commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        mBound = false;
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//

////        try{
////            File file = new File(getDir("data", MODE_PRIVATE), "data_filename");
////            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
////            outputStream.writeObject(mVisits);
////            outputStream.flush();
////            outputStream.close();
////        }catch (Exception e){
////            Log.e("VASI", "Iesi acas scriere", e);
////        }
//    }

    private void onCloudReady() {
        if (mCloud != null && mBound)
            mCloud.fetch();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Cloud.CloudBinder binder = (Cloud.CloudBinder) service;
            mCloud = (Cloud) binder.getService();
            mBound = true;
            onCloudReady();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

}
