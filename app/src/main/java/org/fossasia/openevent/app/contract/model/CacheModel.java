package org.fossasia.openevent.app.contract.model;

public interface CacheModel {

    void saveObject(Object key, Object value);

    Object getValue(Object key);

    void clear();

}
