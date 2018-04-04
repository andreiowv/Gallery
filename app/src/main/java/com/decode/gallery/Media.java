package com.decode.gallery;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucian.cioroga on 3/7/2018.
 */

public class Media implements Parcelable{
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_VIDEO = 1;
    public static final int TYPE_SOUND = 2;

    private String mName;
    private int mType;
    private String mURI;
    private long mDuration;

    private String mArtist;
    private String mAlbum;

    public Media(int type, long duration, String url, String title) {
        mType = type;
        mName = title;
        mURI = url;
        mDuration = duration;
    }

    public String getmArtist() {
        return mArtist;
    }

    public void setmArtist(String mArtist) {
        this.mArtist = mArtist;
    }

    public String getmAlbum() {
        return mAlbum;
    }

    public void setmAlbum(String mAlbum) {
        this.mAlbum = mAlbum;
    }

    public Media(int type, String title, String url, String artist, String album){
        mType = type;
        mName = title;
        mAlbum = album;
        mURI = url;
        mArtist = artist;
    }

    protected Media(Parcel in) {
        mType = in.readInt();

        mDuration = in.readLong();
        mURI = in.readString();
        mName = in.readString();
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

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
        Uri uri = type == TYPE_SOUND ? MediaStore.Audio.Media.EXTERNAL_CONTENT_URI :MediaStore.Files.getContentUri("external");

        String[] projection = {
                MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED, MediaStore.Files.FileColumns.MIME_TYPE, MediaStore.Files.FileColumns.TITLE,
                MediaStore.Video.Media.DURATION, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM
        };

        String[] proj = {MediaStore.Files.FileColumns._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ARTIST};
        String selection= null;

        if (type == TYPE_IMAGE){
           selection =  MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
        }else if (type == TYPE_VIDEO){
            selection =  MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        } else if (type == TYPE_SOUND) {
            selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        }
        CursorLoader cursorLoader = new CursorLoader(context, uri, type==TYPE_SOUND?proj:projection, selection, null, MediaStore.Files.FileColumns.DATE_ADDED + " DESC");

        cursor = cursorLoader.loadInBackground();

        cursor.moveToFirst();

        do {
            if(type == TYPE_SOUND){
                ret.add(new Media(type, cursor.getString(1), cursor.getString(0), cursor.getString(2), cursor.getString(3)));
            }else {
                ret.add(new Media(type, cursor.getLong(5), cursor.getString(1), cursor.getString(4)));
            }
        } while (cursor.moveToNext());

        cursor.close();

        return ret;
    }


    public long getDuration() {
        return mDuration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mType);
        parcel.writeLong(mDuration);
        parcel.writeString(mURI);
        parcel.writeString(mName);
    }
}
