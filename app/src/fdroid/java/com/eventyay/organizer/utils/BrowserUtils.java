package com.eventyay.organizer.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public final class BrowserUtils {

    private BrowserUtils() {
    }

    public static void launchUrl(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }
}
