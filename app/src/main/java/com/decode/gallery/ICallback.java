package com.decode.gallery;

import android.view.View;

/**
 * Created by andrei on 28/02/2018.
 */

public interface ICallback {
    void preview(Media media, View view);
    void showPermissions();
    int getVisits(Media media);

}
