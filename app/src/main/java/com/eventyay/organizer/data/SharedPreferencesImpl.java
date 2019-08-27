package com.eventyay.organizer.data;

import android.content.Context;
import com.eventyay.organizer.OrgaProvider;
import com.eventyay.organizer.common.Constants;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;

public class SharedPreferencesImpl implements Preferences {
    private final android.content.SharedPreferences sharedPreferences;

    @Inject
    SharedPreferencesImpl() {
        sharedPreferences =
                OrgaProvider.context.getSharedPreferences(
                        Constants.FOSS_PREFS, Context.MODE_PRIVATE);
    }

    @Override
    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    @Override
    public void saveString(String key, String value) {
        android.content.SharedPreferences.Editor editor = getEditor();
        editor.putString(key, value);
        editor.apply();
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    @Override
    public void setLong(String key, long value) {
        android.content.SharedPreferences.Editor editor = getEditor();
        editor.putLong(key, value);
        editor.apply();
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        android.content.SharedPreferences.Editor editor = getEditor();
        editor.putBoolean(key, value);
        editor.apply();
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        return sharedPreferences.getStringSet(key, defaultValue);
    }

    @Override
    public void saveStringSet(String key, Set<String> value) {
        android.content.SharedPreferences.Editor editor = getEditor();
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
    public void setInt(String key, int value) {
        android.content.SharedPreferences.Editor editor = getEditor();
        editor.putInt(key, value);
        editor.apply();
    }

    @Override
    public int getInt(String key, int value) {
        return sharedPreferences.getInt(key, value);
    }

    private android.content.SharedPreferences.Editor getEditor() {
        return sharedPreferences.edit();
    }
}
