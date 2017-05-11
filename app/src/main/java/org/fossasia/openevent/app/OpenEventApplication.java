package org.fossasia.openevent.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import org.fossasia.openevent.app.utils.Constants;

public class OpenEventApplication extends Application {

    public String getToken(){
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.FOSS_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constants.SHARED_PREFS_TOKEN,"null");
    }
}
