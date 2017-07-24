package org.fossasia.openevent.app.data.contract;

import java.util.Set;

public interface ISharedPreferenceModel {

    String getString(String key, String defaultValue);

    void saveString(String key, String value);

    long getLong(String key, long defaultValue);

    void setLong(String key, long value);

    Set<String> getStringSet(String key, Set<String> defaultValue);

    void saveStringSet(String key, Set<String> value);

    void addStringSetElement(String key, String value);


}
