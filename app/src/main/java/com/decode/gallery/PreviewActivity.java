package com.decode.gallery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PreviewActivity extends AppCompatActivity{

    private SquareRelativeLayout relativeLayout;
    private ImageView previewThumb;
    private Media media;
    private Picasso mThumbs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        supportPostponeEnterTransition();
        relativeLayout = findViewById(R.id.preview_item);
        previewThumb = findViewById(R.id.preview_thumb);
        media = getIntent().getParcelableExtra("media");
        if (media.getType() == Media.TYPE_IMAGE) {
            mThumbs = new Picasso.Builder(this).build();

            mThumbs.load((!media.getUrl().contains("http") ?"file://" : "") + media.getUrl()).fit().centerCrop().into(previewThumb, new Callback() {
                @Override
                public void onSuccess() {
                    scheduleStartPostponedTransition(previewThumb);
                }

                @Override
                public void onError() {

                }
            });
        } else {
            mThumbs = new Picasso.Builder(this).addRequestHandler(new VideoRequestHandler()).build();
            mThumbs.load("video:" + media.getUrl()).fit().centerCrop().into(previewThumb, new Callback() {
                @Override
                public void onSuccess() {
                    scheduleStartPostponedTransition(previewThumb);
                }

                @Override
                public void onError() {

                }
            });
        }
        //relativeLayout.setBackgroundColor(getIntent().getExtras().getInt("color"));

    }

    private void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        startPostponedEnterTransition();
                        return true;
                    }
                });
    }


    @Override
    public void finish() {
        Intent result = new Intent();
        result.putExtra("media", media);
        setResult(RESULT_OK, result);
        super.finish();
    }

}
