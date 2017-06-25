package org.fossasia.openevent.app.common.binding;

import android.content.res.ColorStateList;
import android.databinding.BindingAdapter;
import android.databinding.BindingConversion;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.utils.CircleTransform;

public class BindingAdapters {

    @BindingConversion
    public static String longToStr(Long value) {
        return value != null ? String.valueOf(value) : "";
    }

    @BindingAdapter({"circleImageUrl"})
    public static void bindCircularImage(ImageView imageView, String url) {
        if(TextUtils.isEmpty(url)) {
            imageView.setImageResource(R.drawable.ic_photo_shutter);
            return;
        }

        Picasso.with(imageView.getContext())
            .load(Uri.parse(url))
            .error(R.drawable.ic_photo_shutter)
            .placeholder(R.drawable.ic_photo_shutter)
            .transform(new CircleTransform())
            .into(imageView);
    }

    @BindingAdapter("backgroundTint")
    public static void setBackgroundTintColor(View view, @ColorInt int color) {
        ViewCompat.setBackgroundTintList(view, ColorStateList.valueOf(color));
    }

    @BindingAdapter("srcCompat")
    public static void bindSrcCompat(FloatingActionButton fab, Drawable drawable){
        fab.setImageDrawable(drawable);
    }

}
