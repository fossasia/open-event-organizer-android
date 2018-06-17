package org.fossasia.openevent.app.data;

import java.util.Set;

public interface Preferences {

    String getString(String key, String defaultValue);

    void saveString(String key, String value);

    long getLong(String key, long defaultValue);

    void setLong(String key, long value);

    boolean getBoolean(String key, boolean defaultValue);

    void setBoolean(String key, boolean value);

    Set<String> getStringSet(String key, Set<String> defaultValue);

    void saveStringSet(String key, Set<String> value);

    void addStringSetElement(String key, String value);

}
