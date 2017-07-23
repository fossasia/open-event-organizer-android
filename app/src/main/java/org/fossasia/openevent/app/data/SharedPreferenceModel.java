package org.fossasia.openevent.app.data;

import android.content.Context;
import android.content.SharedPreferences;

import org.fossasia.openevent.app.data.contract.ISharedPreferenceModel;
import org.fossasia.openevent.app.utils.Constants;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

public class SharedPreferenceModel implements ISharedPreferenceModel {
    private SharedPreferences sharedPreferences;

    @Inject
    SharedPreferenceModel(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.FOSS_PREFS, Context.MODE_PRIVATE);
    }

    @Override
    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    @Override
    public void saveString(String key, String value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString(key, value);
        editor.apply();
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    @Override
    public void setLong(String key, long value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putLong(key, value);
        editor.apply();
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        return sharedPreferences.getStringSet(key, defaultValue);
    }

    @Override
    public void saveStringSet(String key, Set<String> value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putStringSet(key, value);
        editor.apply();
    }

    @Override
    public void addStringSetElement(String key, String value) {
        Set<String> set = getStringSet(key, new HashSet<>());
        set.add(value);
        saveStringSet(key, set);
    }

    private SharedPreferences.Editor getEditor() {
        return sharedPreferences.edit();
    }

}
