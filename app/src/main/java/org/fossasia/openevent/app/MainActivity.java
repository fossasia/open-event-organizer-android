package org.fossasia.openevent.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import org.fossasia.openevent.app.Utils.Constants;

public class MainActivity extends Application {

    public String getToken(){
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.fossPrefs, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Constants.sharedPrefsToken,"null");
        return token;
    }
}
