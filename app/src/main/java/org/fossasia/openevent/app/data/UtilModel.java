package org.fossasia.openevent.app.data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.OrgaProvider;
import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;

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

    private final Context context;
    private final ISharedPreferenceModel sharedPreferenceModel;
    private String token;

    private static boolean blockNetwork;

    @Inject
    UtilModel(ISharedPreferenceModel sharedPreferenceModel) {
        context = OrgaProvider.context;
        this.sharedPreferenceModel = sharedPreferenceModel;
    }

    public static void blockNetwork() {
        UtilModel.blockNetwork = true;
    }

    public static void releaseNetwork() {
        UtilModel.blockNetwork = false;
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
        if (blockNetwork)
            return false;

        ConnectivityManager connectivityManager = (ConnectivityManager) OrgaProvider.context
            .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            return info != null && info.isConnected();
        }
        return false;
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
