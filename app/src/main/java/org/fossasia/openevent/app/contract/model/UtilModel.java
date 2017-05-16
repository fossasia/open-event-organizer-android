package org.fossasia.openevent.app.contract.model;

public interface UtilModel {

    String getResourceString(int stringId);

    String getString(String key, String defaultValue);

    void saveString(String key, String value);

    String getToken();

    void saveToken(String token);

    boolean isConnected();

    boolean isLoggedIn();

    void logout();

}
