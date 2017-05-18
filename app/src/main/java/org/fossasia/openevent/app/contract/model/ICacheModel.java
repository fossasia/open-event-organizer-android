package org.fossasia.openevent.app.contract.model;

public interface ICacheModel {

    void saveObject(Object key, Object value);

    Object getValue(Object key);

    void clear();

}
