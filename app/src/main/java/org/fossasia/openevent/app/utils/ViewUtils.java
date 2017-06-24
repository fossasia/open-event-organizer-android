package org.fossasia.openevent.app.utils;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.view.View;

public class ViewUtils {

    public static void showView(View view, int mode, boolean show) {
        if(show)
            mode = View.VISIBLE;

        view.setVisibility(mode);
    }

    public static void showView(View view, boolean show) {
        showView(view, View.GONE, show);
    }

    public static void setTint(View view, int color) {
        ViewCompat.setBackgroundTintList(view, ColorStateList.valueOf(color));
    }

    public static void setTint(View view, String color) {
        setTint(view, Color.parseColor(color));
    }

}
