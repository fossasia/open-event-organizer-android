package org.fossasia.openevent.app.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.StringRes;

import org.fossasia.openevent.app.contract.model.UtilModel;
import org.fossasia.openevent.app.utils.Constants;
import org.fossasia.openevent.app.utils.Network;

public class AndroidUtilModel implements UtilModel {

    private Context context;
    private SharedPreferences sharedPreferences;
    private String token;

    public AndroidUtilModel(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Constants.FOSS_PREFS, Context.MODE_PRIVATE);
    }

    @Override
    public String getResourceString(@StringRes int stringId) {
        return context.getResources().getString(stringId);
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
    public String getToken() {
        if(token != null)
            return token;
        return getString(Constants.SHARED_PREFS_TOKEN, null);
    }

    @Override
    public void saveToken(String token) {
        saveString(Constants.SHARED_PREFS_TOKEN, token);
    }

    @Override
    public boolean isConnected() {
        return Network.isNetworkConnected(context);
    }

    @Override
    public boolean isLoggedIn() {
        if(token == null)
            token = getToken();
        return token != null;
    }

    @Override
    public void logout() {
        saveToken(null);
    }
}
