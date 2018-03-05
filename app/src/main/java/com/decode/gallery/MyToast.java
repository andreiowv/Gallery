package com.decode.gallery;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

/**
 * Created by andrei on 05/03/2018.
 */

public class MyToast implements View.OnClickListener {
    private Context appContext;

    MyToast(Context applicationContext){
        this.appContext = applicationContext;
    }
    @Override
    public void onClick(View view) {
        Toast toast = Toast.makeText(this.appContext, "Nice Toast", 3);
        toast.show();
    }
}
