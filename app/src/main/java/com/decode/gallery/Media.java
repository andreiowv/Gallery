package com.decode.gallery;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucian.cioroga on 3/7/2018.
 */

public class Media {
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_VIDEO = 1;

    private String mName;
    private int mType;
    private String mURI;
    private long mDuration;

    public Media(int type, long duration, String url, String title) {
        mType = type;
        mName = title;
        mURI = url;
        mDuration = duration;
    }

    public String getName() {
        return mName;
    }

    public int getType() {
        return mType;
    }

    public String getUrl() {
        return mURI;
    }

    public static List<Media> getMedia(int type, Context context) {

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

        }
        List<Media> ret = new ArrayList<>();
        Cursor cursor;
        Uri uri = MediaStore.Files.getContentUri("external");
        String[] projection = {
                MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED, MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.MIME_TYPE, MediaStore.Files.FileColumns.TITLE,
                MediaStore.Video.Media.DURATION
        };

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + (type == TYPE_IMAGE ? MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                : MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
        CursorLoader cursorLoader = new CursorLoader(context, uri, projection, selection, null, MediaStore.Files.FileColumns.DATE_ADDED + " DESC");

        cursor = cursorLoader.loadInBackground();

        cursor.moveToFirst();

        do {
            ret.add(new Media(type, cursor.getLong(6), cursor.getString(1), cursor.getString(5)));
        } while (cursor.moveToNext());

        cursor.close();

        return ret;
    }


    public long getDuration() {
        return mDuration;
    }
}
