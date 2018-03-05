package com.decode.gallery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mBack1Button, mBack2Button, mBack0Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        mBack0Button = (Button) findViewById(R.id.button_back0);
        mBack0Button.setOnClickListener(this);

        mBack1Button = (Button) findViewById(R.id.button_back1);
        mBack1Button.setOnClickListener(this);

        mBack2Button = (Button) findViewById(R.id.button_back2);
        mBack2Button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button_back0) {
            setResult(0);
        } else if (view.getId() == R.id.button_back1){
            setResult(1);
        } else if (view.getId() == R.id.button_back2) {
            setResult(2);
        }
        finish();
    }
}
