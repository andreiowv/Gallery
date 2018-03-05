package com.decode.gallery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by andrei on 28/02/2018.
 */

public class GalleryFragment extends Fragment  implements View.OnClickListener{

    public static int TYPE_IMAGE = 0;
    public static int TYPE_VIDEO = 1;

    private Button mPreviewButton;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.gallery_fragment, container, false);

        mPreviewButton = (Button) root.findViewById(R.id.preview_button);
        int mType = getArguments() != null ? getArguments().getInt("type", 0) : 0;
        mPreviewButton.setText("Preview " + mType);
        mPreviewButton.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View view) {
        int mType = getArguments() != null ? getArguments().getInt("type", 0) : 0;
        if(getActivity() instanceof ICallback && !getActivity().isDestroyed() && !getActivity().isFinishing())
            ((ICallback) getActivity()).preview(mType);

    }


}
