package org.fossasia.openevent.app.utils;

import android.view.View;

public class AndroidUtils {

    public static void showView(View view, boolean show) {
        int mode = View.GONE;

        if(show)
            mode = View.VISIBLE;

        view.setVisibility(mode);
    }

}
