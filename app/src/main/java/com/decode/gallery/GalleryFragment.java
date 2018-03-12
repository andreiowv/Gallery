package com.decode.gallery;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

public class GalleryFragment extends Fragment implements View.OnClickListener {

    private int mType;
    private RecyclerView mRecy;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.gallery_fragment, container, false);

        mType = getArguments() != null ? getArguments().getInt("type", 0) : 0;
        mRecy = root.findViewById(R.id.my_recycler_view);

        mRecy.setLayoutManager(new GridLayoutManager(getContext(), getResources().getInteger(R.integer.columns_port)));
        mRecy.setAdapter(new Adapter(mType));

        return root;
    }

    @Override
    public void onClick(View view) {
        if (view.getTag() != null && view.getTag() instanceof Media) {
            if (getActivity() instanceof ICallback && !getActivity().isDestroyed() && !getActivity().isFinishing()) {
                Media media = (Media) view.getTag();
                ((ICallback) getActivity()).preview(media);
            }
        }


    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mLabel;
        public ImageView mThumb;

        public ViewHolder(View itemView) {
            super(itemView);

            mLabel = itemView.findViewById(R.id.item_media_tv);
            mThumb = itemView.findViewById(R.id.thumb);
        }
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private List<Media> mMedia;
        private Picasso mThumbs;
        private int mType;

        public Adapter(int mType) {
            this.mType = mType;
            this.mMedia = Media.getMedia(mType, getContext());
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(this.mType == Media.TYPE_IMAGE){
                mThumbs = new Picasso.Builder(getContext()).build();
            } else {
               mThumbs = new Picasso.Builder(getContext()).addRequestHandler(new VideoRequestHandler()).build();
            }


            LayoutInflater in = LayoutInflater.from(getContext());
            View v = in.inflate(R.layout.item_media, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder vh, int position) {
            vh.itemView.setTag(mMedia.get(position));
            vh.itemView.setOnClickListener(GalleryFragment.this);

            vh.mLabel.setText(U.format(mMedia.get(position).getDuration()));
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


