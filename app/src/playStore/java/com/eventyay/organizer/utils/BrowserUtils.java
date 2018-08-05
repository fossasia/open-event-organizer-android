package com.eventyay.organizer.utils;

import android.content.Context;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;

public final class BrowserUtils {

    private BrowserUtils() {
    }

    public static void launchUrl(String url, Context context) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(context, Uri.parse(url));
    }
}
