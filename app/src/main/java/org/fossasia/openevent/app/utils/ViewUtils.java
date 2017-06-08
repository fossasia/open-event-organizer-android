package org.fossasia.openevent.app.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.View;

public class ViewUtils {

    public static void showView(View view, boolean show) {
        int mode = View.GONE;

        if(show)
            mode = View.VISIBLE;

        view.setVisibility(mode);
    }

    public static void setTint(View view, int color) {
        ViewCompat.setBackgroundTintList(view, ColorStateList.valueOf(color));
    }

    public static void setTint(View view, String color) {
        setTint(view, Color.parseColor(color));
    }

    public static int convertDpToPixel(float dp, Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int)px;
    }

}
