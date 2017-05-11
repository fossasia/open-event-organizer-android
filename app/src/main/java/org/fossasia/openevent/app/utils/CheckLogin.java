package org.fossasia.openevent.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class CheckLogin {
    public static String isLogin(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.FOSS_PREFS, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Constants.SHARED_PREFS_TOKEN,"null");
        return token;
    }
}
