package com.decode.gallery;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by andrei on 28/03/2018.
 */

public class Cloud extends Service {

    public static final String ACTION_CLOUD = "com.decode.gallery.action-cloud";

    private DB.Helper mDB;
    private Executor mExecutor;
    private final IBinder mBinder = new CloudBinder();

    @Override
    public IBinder onBind(Intent intent) {
        mDB = new DB.Helper(getApplicationContext());
        mExecutor = Executors.newSingleThreadExecutor();
        return mBinder;
    }

    public class CloudBinder extends Binder {
        Service getService() {
            return Cloud.this; // return this instance of Cloud so clients can call public methods
        }
    }

    public void fetch() {

        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Photo[] photos = U.api("https://goo.gl/xATgGr", Photo[].class);
                SQLiteDatabase db = mDB.getWritableDatabase();

                // 3. store to DB
                db.execSQL(DB.CloudPhoto.SQL_DROP);
                db.execSQL(DB.CloudPhoto.SQL_CREATE);
                for (Photo p : photos) {
                    ContentValues values = new ContentValues();
                    values.put(DB.CloudPhoto.Entry.COLUMN_URL, p.url);
                    values.put(DB.CloudPhoto.Entry.COLUMN_TITLE, p.title);
                    db.insert(DB.CloudPhoto.Entry.TABLE_NAME, null, values);
                }
                // 4. notify
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(ACTION_CLOUD));
            }
        });
        // 1. access api: https://goo.gl/xATgGr
        // 2. interpret json


    }

    private static class Photo {
        public String url;
        public String title;
    }

}
