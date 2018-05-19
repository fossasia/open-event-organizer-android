package org.fossasia.openevent.app.data;

import android.content.Context;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;

import javax.inject.Inject;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import io.reactivex.Completable;
import timber.log.Timber;

/**
 * Utility class to be used by presenters and models for
 * Android Context based actions
 *
 * Break in sub-modules if grows large
 */
public class AndroidUtils implements ContextUtils {

    private final Context context;

    @Inject
    AndroidUtils(Context context) {
        this.context = context;
    }

    @Override
    public String getResourceString(@StringRes int stringId) {
        return context.getResources().getString(stringId);
    }

    @Override
    @ColorInt
    public int getResourceColor(@ColorRes int colorId) {
        return ContextCompat.getColor(context, colorId);
    }

    @Override
    public Completable deleteDatabase() {
        String dbName = OrgaDatabase.NAME + ".db";

        return Completable.fromAction(() -> {
            OrgaApplication.destroyDatabase();
            context.deleteDatabase(dbName);
            OrgaApplication.initializeDatabase(context);
        }).doOnComplete(() ->
            Timber.d("Database %s deleted on Thread %s", dbName, Thread.currentThread().getName())
        );
    }
}
