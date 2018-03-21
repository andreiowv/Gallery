package com.decode.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by andrei on 28/02/2018.
 */

public class GalleryFragment extends Fragment implements View.OnClickListener, Permissions.Callback {

    private int mType;
    private RecyclerView mRecy;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.gallery_fragment, container, false);

        mType = getArguments() != null ? getArguments().getInt("type", 0) : 0;
        mRecy = root.findViewById(R.id.my_recycler_view);

        mRecy.setLayoutManager(new GridLayoutManager(getContext(), getResources().getInteger(R.integer.columns_port)));

        load();
        return root;
    }
    private void load() {
        Permissions.check(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE, 555, this);
    }


    @Override
    public void onClick(View view) {
        if (view.getTag() != null && view.getTag() instanceof Media) {
            if (getActivity() instanceof ICallback && !getActivity().isDestroyed() && !getActivity().isFinishing()) {
                Media media = (Media) view.getTag();
                ((ICallback) getActivity()).preview(media, view);
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 555)
            Permissions.onPermissionsRequestResult(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE, permissions, grantResults, this);;


    }

    private void loadAdapter(){
        mRecy.setAdapter(new Adapter(mType));
    }

    @Override
    public void onPermissionNeedMoreInfo(String permission) {
        Snackbar.make(getActivity().findViewById(R.id.my_FabParent), "Here's a Snackbar", Snackbar.LENGTH_LONG)
                .setAction("Action", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Permissions.request(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE, 555);
                    }
                }).show();
    }

    @Override
    public void onPermissionNeverAskAgain(String permission) {
        Snackbar.make(getActivity().findViewById(R.id.my_FabParent), "Here's a Snackbar", Snackbar.LENGTH_LONG)
                .setAction("Action", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Permissions.settings(getActivity());
                    }
                }).show();
    }

    @Override
    public void onPermissionAllowed(String permission) {
        loadAdapter();
    }

    @Override
    public void onPermissionDenied(String permission) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1
                && resultCode == Activity.RESULT_OK)
            mRecy.getAdapter().notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mLabel;
        public ImageView mThumb;
        private TextView mVisits;

        public ViewHolder(View itemView) {
            super(itemView);

            mLabel = itemView.findViewById(R.id.item_media_tv);
            mThumb = itemView.findViewById(R.id.thumb);
            mVisits = itemView.findViewById(R.id.visits);
        }
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private List<Media> mMedia;
        private Picasso mThumbs;
        private int mType;

        public Adapter(int mType) {
            this.mType = mType;
            this.mMedia = Media.getMedia(mType, getContext());
            if(this.mType == Media.TYPE_IMAGE){
                mThumbs = new Picasso.Builder(getContext()).build();
            } else {
                mThumbs = new Picasso.Builder(getContext()).addRequestHandler(new VideoRequestHandler()).build();
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater in = LayoutInflater.from(getContext());
            View v = in.inflate(R.layout.item_media, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder vh, int position) {

            ICallback gallery = (ICallback) getActivity();
            vh.mVisits.setVisibility(gallery.getVisits(mMedia.get(position)) > 0 ? View.VISIBLE : View.GONE);
            vh.mVisits.setText("" + gallery.getVisits(mMedia.get(position)));
            vh.itemView.setTag(mMedia.get(position));
            vh.itemView.setOnClickListener(GalleryFragment.this);

            vh.mLabel.setText(mType == Media.TYPE_VIDEO ? U.format(mMedia.get(position).getDuration()) : mMedia.get(position).getName());
            if (mMedia.get(position).getType() == Media.TYPE_IMAGE) {
                mThumbs.load("file://" + mMedia.get(position).getUrl()).fit().centerCrop().into(vh.mThumb);
            } else {
                mThumbs.load("video:" + mMedia.get(position).getUrl()).fit().centerCrop().into(vh.mThumb);
            }
        }

        @Override
        public int getItemCount() {
            return mMedia.size();
        }
    }

}


