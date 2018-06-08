package org.fossasia.openevent.app.ui.binding;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.databinding.BindingAdapter;
import android.databinding.BindingConversion;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.databinding.InverseMethod;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.ui.ViewUtils;
import org.fossasia.openevent.app.ui.views.DatePicker;
import org.fossasia.openevent.app.ui.views.TimePicker;
import org.fossasia.openevent.app.utils.Utils;

@SuppressWarnings("PMD.AvoidCatchingGenericException")
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

    @InverseMethod("strToDouble")
    public static String doubleToStr(Double value) {
        return value == null ? "" : String.valueOf(value);
    }

    @BindingConversion
    @InverseMethod("strToInteger")
    public static String integerToStr(Integer value) {
        return value == null ?  "" : String.valueOf(value);
    }

    @SuppressWarnings("PMD")
    public static Long strToLong(String value) {
        return Utils.isEmpty(value) ?  null : Long.parseLong(value);
    }

    @SuppressWarnings("PMD")
    public static Float strToFloat(String value) {
        return Utils.isEmpty(value) ? null : Float.parseFloat(value);
    }

    @SuppressWarnings("PMD")
    public static Double strToDouble(String value) {
        return Utils.isEmpty(value) ? null : Double.parseDouble(value);
    }

    @SuppressWarnings("PMD.NullAssignment")
    public static Integer strToInteger(String value) {
        return Utils.isEmpty(value) ? null : Integer.parseInt(value);
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
                ViewUtils.hideKeyboard(editText);
                runnable.run();
                return true;
            }
            return false;
        });
    }

    @BindingAdapter(value = "valueAttrChanged")
    public static void setDateChangeListener(DatePicker datePicker, final InverseBindingListener listener) {
        if (listener != null) {
            datePicker.setOnDateChangedListener(newDate -> listener.onChange());
        }
    }

    @InverseBindingAdapter(attribute = "value")
    public static String getRealValue(DatePicker datePicker) {
        return datePicker.getValue().get();
    }

    @BindingAdapter(value = "valueAttrChanged")
    public static void setTimeListener(TimePicker timePicker, final InverseBindingListener listener) {
        if (listener != null) {
            timePicker.setOnDateChangedListener(newDate -> listener.onChange());
        }
    }

    @InverseBindingAdapter(attribute = "value")
    public static String getRealValue(TimePicker timePicker) {
        return timePicker.getValue().get();
    }

    @BindingAdapter("imageOnClick")
    public static void bindOnImageButtonClickListener(ImageButton imageButton, String url) {
        imageButton.setOnClickListener(view -> {
            if (url != null) {
                Context context = imageButton.getContext();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "No Web browser found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
