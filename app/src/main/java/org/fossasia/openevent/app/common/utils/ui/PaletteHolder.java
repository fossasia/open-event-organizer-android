package org.fossasia.openevent.app.common.utils.ui;

import android.graphics.drawable.Drawable;
import android.support.v4.util.SparseArrayCompat;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.florent37.glidepalette.GlidePalette;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.functions.Consumer;
import timber.log.Timber;

// TODO: Reimplement once Glide Palette is fixed
@SuppressWarnings("PMD.AvoidCatchingGenericException")
public final class PaletteHolder {

    private static PaletteHolder paletteHolderInstance;
    private final Map<String, Holder> holderMap = new ConcurrentHashMap<>();

    private static class Holder {
        private WeakReference<GlidePalette<Drawable>> glidePalette;
        private final SparseArrayCompat<WeakReference<ViewGroup>> headerMap = new SparseArrayCompat<>();
        private final SparseArrayCompat<WeakReference<TextView>> textMap = new SparseArrayCompat<>();

        Holder(GlidePalette<Drawable> glidePalette) {
            this.glidePalette = new WeakReference<>(glidePalette);
        }

        void setGlidePalette(GlidePalette<Drawable> glidePalette) {
            this.glidePalette = new WeakReference<>(glidePalette);
        }
    }

    private PaletteHolder() {
        // Never Called
    }

    public static PaletteHolder getInstance() {
        synchronized (PaletteHolder.class) {

            if (paletteHolderInstance == null)
                paletteHolderInstance = new PaletteHolder();

            return paletteHolderInstance;
        }
    }

    public void setPalette(String key, GlidePalette<Drawable> glidePalette) {
        synchronized (this) {
            Holder holder;
            if (holderMap.containsKey(key)) {
                holder = holderMap.get(key);
                holder.setGlidePalette(glidePalette);
            } else {
                holder = new Holder(glidePalette);
                holderMap.put(key, holder);
            }

            setRemainingHeaders(holder);
            setRemainingTexts(holder);
        }
    }

    public void registerHeader(String key, ViewGroup viewGroup) {
        synchronized (this) {
            Holder holder = getHolder(key);

            holder.headerMap.put(viewGroup.getId(), new WeakReference<>(viewGroup));

            if (holder.glidePalette != null) {
                setHeaderColor(holder.glidePalette, viewGroup);
            }
        }
    }

    public void registerText(String key, TextView textView) {
        synchronized (this) {
            Holder holder = getHolder(key);
            holder.textMap.put(textView.getId(), new WeakReference<>(textView));

            if (holder.glidePalette != null) {
                setTextColor(holder.glidePalette, textView);
            }
        }
    }

    private <T> void setColor(SparseArrayCompat<WeakReference<T>> sparseArray, Consumer<T> consumer) {
        for (int i = 0; i < sparseArray.size(); i++) {
            T t = sparseArray.valueAt(i).get();

            if (t != null)
                try {
                    consumer.accept(t);
                } catch (Exception e) {
                    Timber.d(e);
                }
        }
    }

    private void setRemainingHeaders(Holder holder) {
        setColor(holder.headerMap, viewGroup -> setHeaderColor(holder.glidePalette, viewGroup));
    }

    private void setRemainingTexts(Holder holder) {
        setColor(holder.textMap, textView -> setTextColor(holder.glidePalette, textView));
    }

    private void setHeaderColor(WeakReference<GlidePalette<Drawable>> glidePalette, ViewGroup viewGroup) {
        if (glidePalette == null || viewGroup == null)
            return;

        if (glidePalette.get() == null)
            return;

        glidePalette.get().intoBackground(viewGroup);
    }

    private void setTextColor(WeakReference<GlidePalette<Drawable>> glidePalette, TextView textView) {
        if (glidePalette == null || textView == null)
            return;

        if (glidePalette.get() == null)
            return;

        glidePalette.get().intoTextColor(textView, GlidePalette.Swatch.TITLE_TEXT_COLOR);
    }

    private Holder getHolder(String key) {
        Holder holder = holderMap.get(key);

        if (holder == null) {
            holder = new Holder(null);
            holderMap.put(key, holder);
        }

        return holder;
    }

}
