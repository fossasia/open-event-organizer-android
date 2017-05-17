package org.fossasia.openevent.app.contract.model;

public interface IUtilModel {

    String getString(String key, String defaultValue);

    void saveString(String key, String value);

    boolean isConnected();

}
