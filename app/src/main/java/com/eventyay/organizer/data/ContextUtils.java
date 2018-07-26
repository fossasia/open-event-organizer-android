package com.eventyay.organizer.data;

import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;

import io.reactivex.Completable;

public interface ContextUtils {

    String getResourceString(@StringRes int stringId);

    @ColorInt int getResourceColor(@ColorRes int colorId);

    Completable deleteDatabase();

}
