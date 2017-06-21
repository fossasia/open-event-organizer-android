package org.fossasia.openevent.app.data.contract;

import io.reactivex.Completable;

public interface IUtilModel {

    String getResourceString(int stringId);

    String getString(String key, String defaultValue);

    void saveString(String key, String value);

    String getToken();

    void saveToken(String token);

    boolean isConnected();

    Completable deleteDatabase();

}
