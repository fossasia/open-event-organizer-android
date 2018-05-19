package org.fossasia.openevent.app.core.event.about;

import android.content.res.Resources;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.core.view.ViewCompat;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

class ToolbarColorChanger {

    private com.google.android.material.appbar.AppBarLayout appBarLayout;
    private AppBarLayout.OnOffsetChangedListener onOffsetChangedListener;

    @Inject
    ToolbarColorChanger() {
        // Default Implementation
    }

    void removeChangeListener() {
        if (appBarLayout != null && onOffsetChangedListener != null)
            appBarLayout.removeOnOffsetChangedListener(onOffsetChangedListener);
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    Observable<Integer> observeColor(AppBarLayout appBar, CollapsingToolbarLayout collapsingToolbar) {
        appBarLayout = appBar;
        Resources resources = appBar.getResources();
        PublishSubject<Boolean> offsetChanger = PublishSubject.create();

        onOffsetChangedListener = (appBarLayout, verticalOffset) ->
            offsetChanger.onNext((collapsingToolbar.getHeight() + verticalOffset) <
                (2 * ViewCompat.getMinimumHeight(collapsingToolbar)));
        appBar.addOnOffsetChangedListener(onOffsetChangedListener);

        return offsetChanger.distinctUntilChanged()
            .map(collapsed -> {
                if (collapsed)
                    return resources.getColor(android.R.color.black);
                else
                    return resources.getColor(android.R.color.white);
            });
    }

}
