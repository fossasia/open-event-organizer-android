package org.fossasia.openevent.app.common.app.binding;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.databinding.BindingAdapter;
import android.databinding.BindingConversion;
import android.databinding.InverseMethod;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.utils.ui.CircleTransform;
import org.fossasia.openevent.app.module.main.MainActivity;

public final class BindingAdapters {

    private BindingAdapters() {
        // Never Called
    }

    @BindingConversion
    @InverseMethod("strToLong")
    public static String longToStr(Long value) {
        return value == null ?  "" : String.valueOf(value);
    }

    @BindingConversion
    @InverseMethod("strToFloat")
    public static String floatToStr(Float value) {
        return value == null ?  "" : String.valueOf(value);
    }

    public static Long strToLong(String value) {
        return value == null ?  null : Long.parseLong(value);
    }

    public static Float strToFloat(String value) {
        return value == null ?  null : Float.parseFloat(value);
    }

    @InverseMethod("getType")
    public static int toId(String ticketType) {
        if (ticketType == null)
            return R.id.free;

        switch (ticketType) {
            case "free":
                return R.id.free;
            case "paid":
                return R.id.paid;
            case "donation":
                return R.id.donation;
            default:
                return -1;
        }
    }

    public static String getType(int id) {
        switch (id) {
            case R.id.free:
                return "free";
            case R.id.paid:
                return "paid";
            case R.id.donation:
                return "donation";
            default:
                return "free";
        }
    }

    @BindingAdapter(value = {"imageUrl", "placeholder"}, requireAll = false)
    public static void bindDefaultImage(ImageView imageView, String url, Drawable drawable) {
        if (TextUtils.isEmpty(url)) {
            if (drawable != null)
                imageView.setImageDrawable(drawable);
            return;
        }

        RequestCreator requestCreator = Picasso.with().load(Uri.parse(url));

        if (drawable != null) {
            requestCreator
                .placeholder(drawable)
                .error(drawable);
        }

        requestCreator
            .tag(MainActivity.class)
            .into(imageView);
    }

    @BindingAdapter("circleImageUrl")
    public static void bindCircularImage(ImageView imageView, String url) {
        if (TextUtils.isEmpty(url)) {
            imageView.setImageResource(R.drawable.ic_photo_shutter);
            return;
        }

        Picasso.with()
            .load(Uri.parse(url))
            .error(R.drawable.ic_photo_shutter)
            .placeholder(R.drawable.ic_photo_shutter)
            .transform(new CircleTransform())
            .tag(MainActivity.class)
            .into(imageView);
    }

    @BindingAdapter("tint")
    public static void setTintColor(ImageView imageView, @ColorInt int color) {
        DrawableCompat.setTint(imageView.getDrawable(), color);
    }

    @BindingAdapter("backgroundTint")
    public static void setBackgroundTintColor(View view, @ColorInt int color) {
        ViewCompat.setBackgroundTintList(view, ColorStateList.valueOf(color));
    }

    @BindingAdapter("srcCompat")
    public static void bindSrcCompat(FloatingActionButton fab, Drawable drawable) {
        fab.setImageDrawable(drawable);
    }

    @BindingAdapter("srcCompat")
    public static void bindSrcImageView(ImageView imageView, Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }

    @BindingAdapter("progress_with_animation")
    public static void bindCircularProgress(CircularProgressBar circularProgressBar, int progress) {
        circularProgressBar.setProgressWithAnimation(progress, 500);
    }

    @BindingAdapter("circular_progress_color")
    public static void bindCircularProgressColor(CircularProgressBar circularProgressBar, String colorName) {
        Context context = circularProgressBar.getContext();
        Resources resources = circularProgressBar.getResources();

        int color = ContextCompat.getColor(context, resources.getIdentifier(colorName + "_500", "color", context.getPackageName()));
        int bgColor = ContextCompat.getColor(context, resources.getIdentifier(colorName + "_100", "color", context.getPackageName()));

        circularProgressBar.setColor(color);
        circularProgressBar.setBackgroundColor(bgColor);
    }

    @BindingAdapter("go")
    public static void doneAction(EditText editText, Runnable runnable) {
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                runnable.run();
                return true;
            }
            return false;
        });
    }

}
