package com.eventyay.organizer.data;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;

import io.reactivex.Completable;

public interface ContextUtils {

    String getResourceString(@StringRes int stringId);

    @ColorInt
    int getResourceColor(@ColorRes int colorId);

    Completable deleteDatabase();
}
