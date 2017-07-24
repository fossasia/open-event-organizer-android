package org.fossasia.openevent.app.common.data;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.common.data.contract.ISharedPreferenceModel;
import org.fossasia.openevent.app.common.data.contract.IUtilModel;
import org.fossasia.openevent.app.common.data.db.configuration.OrgaDatabase;
import org.fossasia.openevent.app.common.utils.core.service.NetworkUtils;

import javax.inject.Inject;

import io.reactivex.Completable;
import timber.log.Timber;

/**
 * Utility class to be used by presenters and models for
 * Android Context based actions
 *
 * Break in sub-modules if grows large
 */
public class UtilModel implements IUtilModel {

    private Context context;
    private ISharedPreferenceModel sharedPreferenceModel;
    private String token;

    @Inject
    UtilModel(Context context, ISharedPreferenceModel sharedPreferenceModel) {
        this.context = context;
        this.sharedPreferenceModel = sharedPreferenceModel;
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
    public String getToken() {
        if (token != null)
            return token;

        return sharedPreferenceModel.getString(Constants.SHARED_PREFS_TOKEN, null);
    }

    @Override
    public void saveToken(String token) {
        this.token = token;
        sharedPreferenceModel.saveString(Constants.SHARED_PREFS_TOKEN, token);
    }

    @Override
    public boolean isConnected() {
        return NetworkUtils.isNetworkConnected(context);
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
