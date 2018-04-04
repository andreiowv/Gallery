package com.decode.gallery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by andrei on 28/02/2018.
 */

public class SoundGalleryFragment extends Fragment implements View.OnClickListener{

    private int mType;
    private RecyclerView mRecy;
    private BroadcastReceiver mReceiver;
    private DB.Helper mDB;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.gallery_fragment, container, false);

        mType = getArguments() != null ? getArguments().getInt("type", 0) : 0;
        mRecy = root.findViewById(R.id.my_recycler_view);
        mDB = new DB.Helper(getContext());

        mRecy.setLayoutManager(new GridLayoutManager(getContext(), 1));

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent i) {
                load();
            }
        };

        load();
        return root;
    }


    @Override
    public void onClick(View view) {
//        if (view.getTag() != null && view.getTag() instanceof Media) {
//            if (getActivity() instanceof ICallback && !getActivity().isDestroyed() && !getActivity().isFinishing()) {
//                Media media = (Media) view.getTag();
//                ((ICallback) getActivity()).preview(media, view);
//            }
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(Cloud.ACTION_CLOUD);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, filter);

    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
    }

    private void load(){
        SQLiteDatabase db = mDB.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DB.CloudPhoto.Entry.TABLE_NAME, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            List<Media> media = new ArrayList<>();
            do {
                Media m = new Media(0, 0, cursor.getString(cursor.getColumnIndex(DB.CloudPhoto.Entry.COLUMN_URL)), cursor.getString(cursor.getColumnIndex(DB.CloudPhoto.Entry.COLUMN_TITLE)));
                media.add(m);
            } while (cursor.moveToNext());

            mRecy.setAdapter(new Adapter());
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1
                && resultCode == Activity.RESULT_OK)
            mRecy.getAdapter().notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        private TextView album;
        private TextView artist;
        private TextView mVisits;


        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.sound_title);
            album = itemView.findViewById(R.id.album);
            artist = itemView.findViewById(R.id.artist);
            mVisits = itemView.findViewById(R.id.sound_visits);
        }
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private List<Media> mMedia;

        public Adapter() {
            this.mMedia = Media.getMedia(Media.TYPE_SOUND, getContext());
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater in = LayoutInflater.from(getContext());
            View v = in.inflate(R.layout.item_sount, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder vh, int position) {

            ICallback gallery = (ICallback) getActivity();
            vh.mVisits.setVisibility(gallery.getVisits(mMedia.get(position)) > 0 ? View.VISIBLE : View.GONE);
            vh.mVisits.setText("" + gallery.getVisits(mMedia.get(position)));
            vh.itemView.setTag(mMedia.get(position));
            vh.itemView.setOnClickListener(SoundGalleryFragment.this);

            vh.title.setText(mMedia.get(position).getName());
            vh.album.setText(mMedia.get(position).getmAlbum());
            vh.artist.setText( "by " + mMedia.get(position).getmArtist());

        }

        @Override
        public int getItemCount() {
            return mMedia.size();
        }
    }

}


