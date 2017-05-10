package org.fossasia.openevent.app.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by rishabhkhanna on 26/04/17.
 */

public class CheckLogin {
    public static String isLogin(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.fossPrefs , Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Constants.sharedPrefsToken,"null");
        return token;
    }
}
