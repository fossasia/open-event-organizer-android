package org.fossasia.openevent.app.data;

import android.content.Context;
import android.content.SharedPreferences;

import org.fossasia.openevent.app.contract.model.IUtilModel;
import org.fossasia.openevent.app.utils.Constants;
import org.fossasia.openevent.app.utils.Network;

public class AndroidUtilModel implements IUtilModel {

    private Context context;
    private SharedPreferences sharedPreferences;

    public AndroidUtilModel(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Constants.FOSS_PREFS, Context.MODE_PRIVATE);
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
    public boolean isConnected() {
        return Network.isNetworkConnected(context);
    }
}
