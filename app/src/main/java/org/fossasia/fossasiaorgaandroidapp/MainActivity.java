package org.fossasia.fossasiaorgaandroidapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.fossasia.fossasiaorgaandroidapp.Utils.Constants;

public class MainActivity extends Application {

    public String getToken(){
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.fossPrefs, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Constants.sharedPrefsToken,"null");
        return token;
    }
}
