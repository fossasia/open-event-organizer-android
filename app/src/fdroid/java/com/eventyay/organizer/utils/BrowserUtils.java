package com.eventyay.organizer.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.eventyay.organizer.R;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

public final class BrowserUtils {

    private BrowserUtils() {
    }

    public static void launchUrl(Context context, String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        new CustomTabsIntent.Builder()
            .setToolbarColor(ContextCompat.getColor(context, R.color.color_primary_dark))
            .setCloseButtonIcon(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_arrow_back_black))
            .build()
            .launchUrl(context, Uri.parse(url));
    }
}
