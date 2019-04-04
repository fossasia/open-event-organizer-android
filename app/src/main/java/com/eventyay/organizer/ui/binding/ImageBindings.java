package com.eventyay.organizer.ui.binding;

import androidx.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.github.florent37.glidepalette.GlidePalette;

import com.eventyay.organizer.ui.GlideApp;
import com.eventyay.organizer.ui.GlideRequest;

import io.reactivex.functions.Consumer;
import timber.log.Timber;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

@SuppressWarnings("PMD.AvoidCatchingGenericException")
public final class ImageBindings {

    private ImageBindings() {
        // Never Called
    }

    private static void setGlideImage(ImageView imageView, String url, Drawable drawable, Consumer<GlideRequest<Drawable>> consumer) {
        if (TextUtils.isEmpty(url)) {
            if (drawable != null)
                imageView.setImageDrawable(drawable);
            return;
        }
        GlideRequest<Drawable> request = GlideApp
            .with(imageView.getContext())
            .load(Uri.parse(url));

        if (drawable != null) {
            request
                .placeholder(drawable)
                .error(drawable);
        }
        request
            .centerCrop()
            .transition(withCrossFade());

        if (consumer != null)
            try {
                consumer.accept(request);
            } catch (Exception e) {
                Timber.e(e);
            }

        request.into(imageView);
    }

    @BindingAdapter(value = {"imageUrl", "placeholder"}, requireAll = false)
    public static void bindDefaultImage(ImageView imageView, String url, Drawable drawable) {
        setGlideImage(imageView, url, drawable, null);
    }

    @BindingAdapter(value = {"circleImageUrl", "placeholder"}, requireAll = false)
    public static void bindCircularImage(ImageView imageView, String url, Drawable drawable) {
        setGlideImage(imageView, url, drawable, request -> request.transform(new CircleCrop()));
    }

    @BindingAdapter(value = {"paletteImageUrl", "placeholder", "paletteKey", "paletteProfile"}, requireAll = false)
    public static void bindImageWithPalette(ImageView imageView, String url, Drawable drawable, String key, int profile) {
        if (key == null)
            throw new IllegalArgumentException("A non null 'paletteKey' attribute must be provided for paletteImageUrl");

        GlidePalette<Drawable> glidePalette = GlidePalette
            .with(url)
            .use(profile)
            .crossfade(true);

        setGlideImage(imageView, url, drawable, request -> {
            request.listener(glidePalette);
            //PaletteHolder.getInstance().setPalette(key, glidePalette);
        });
    }

    @BindingAdapter("paletteHeader")
    public static void bindHeaderWithPalette(ViewGroup viewGroup, String key) {
        //PaletteHolder.getInstance().registerHeader(key, viewGroup);
    }

    @BindingAdapter("paletteText")
    public static void bindTextWithPalette(TextView textView, String key) {
        //PaletteHolder.getInstance().registerText(key, textView);
    }

}
