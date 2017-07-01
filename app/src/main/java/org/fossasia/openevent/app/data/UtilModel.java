package org.fossasia.openevent.app.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;
import org.fossasia.openevent.app.utils.Constants;
import org.fossasia.openevent.app.utils.NetworkUtils;

import java.util.HashSet;
import java.util.Set;

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
    private SharedPreferences sharedPreferences;
    private String token;

    @Inject
    UtilModel(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Constants.FOSS_PREFS, Context.MODE_PRIVATE);
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
    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    @Override
    public void saveString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        return sharedPreferences.getStringSet(key, defaultValue);
    }

    @Override
    public void saveStringSet(String key, Set<String> value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(key, value);
        editor.apply();
    }

    @Override
    public void addStringSetElement(String key, String value) {
        Set<String> set = getStringSet(key, new HashSet<>());
        set.add(value);
        saveStringSet(key, set);
    }

    @Override
    public String getToken() {
        if (token != null)
            return token;

        return getString(Constants.SHARED_PREFS_TOKEN, null);
    }

    @Override
    public void saveToken(String token) {
        this.token = token;
        saveString(Constants.SHARED_PREFS_TOKEN, token);
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
