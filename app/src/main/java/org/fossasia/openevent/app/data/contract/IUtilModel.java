package org.fossasia.openevent.app.data.contract;

import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import io.reactivex.Completable;

public interface IUtilModel {

    String getResourceString(@StringRes int stringId);

    @ColorInt int getResourceColor(@ColorRes int colorId);

    String getString(String key, String defaultValue);

    void saveString(String key, String value);

    String getToken();

    void saveToken(String token);

    boolean isConnected();

    Completable deleteDatabase();

}
