package org.fossasia.openevent.app.common.binding;

import android.content.res.ColorStateList;
import android.databinding.BindingAdapter;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.utils.CircleTransform;

public class BindingAdapters {

    @BindingAdapter({"circleImageUrl"})
    public static void bindCircularImage(ImageView imageView, String url) {
        if(TextUtils.isEmpty(url))
            return;

        Picasso.with(imageView.getContext())
            .load(Uri.parse(url))
            .error(R.drawable.ic_photo_shutter)
            .placeholder(R.drawable.ic_photo_shutter)
            .transform(new CircleTransform())
            .into(imageView);
    }

    @BindingAdapter({"backgroundTint"})
    public static void setBackgroundTint(View view, @ColorRes int color) {
        ViewCompat.setBackgroundTintList(view, ColorStateList.valueOf(ContextCompat.getColor(view.getContext(), color)));
    }

}
