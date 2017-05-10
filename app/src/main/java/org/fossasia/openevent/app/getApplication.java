package org.fossasia.openevent.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import org.fossasia.openevent.app.Utils.Constants;

/**
 * Created by rishabhkhanna on 26/04/17.
 */

public class getApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public String getToken(){
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.fossPrefs, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Constants.sharedPrefsToken,"null");
        return token;
    }
}
