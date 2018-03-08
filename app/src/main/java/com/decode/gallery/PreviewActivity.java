package com.decode.gallery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener{

    private SquareRelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        relativeLayout = findViewById(R.id.preview_item);
        relativeLayout.setBackgroundColor(getIntent().getExtras().getInt("color"));

    }

    @Override
    public void onClick(View view) {

        finish();
    }
}
