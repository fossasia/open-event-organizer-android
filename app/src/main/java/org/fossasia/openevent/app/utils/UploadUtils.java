package org.fossasia.openevent.app.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Base64;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayOutputStream;

public final class UploadUtils {

    private UploadUtils() {
        //Never Called
    }

    public static String getMimeTypeFromUri(ContentResolver contentResolver, Uri uri) {
        if (uri == null)
            return null;

        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    //method to convert the selected image to base64 encoded string

    public static String convertBitmapToString(Bitmap bitmap, String type) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        StringBuilder encodedImage = new StringBuilder();
        encodedImage.append(Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP | Base64.URL_SAFE));
        encodedImage.insert(0, "data:image/" + type + ";base64,");

        return encodedImage.toString();
    }
}
