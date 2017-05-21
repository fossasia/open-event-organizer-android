package org.fossasia.openevent.app.data.cache;

public interface ICacheModel {

    void saveObject(Object key, Object value);

    Object getValue(Object key);

    int getSize();

    void clear();

}
