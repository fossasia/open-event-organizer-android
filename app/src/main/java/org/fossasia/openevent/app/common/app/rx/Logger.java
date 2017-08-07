package org.fossasia.openevent.app.common.app.rx;

import android.support.annotation.NonNull;

import java.util.List;

import timber.log.Timber;

public final class Logger {

    public static final String TEST_MESSAGE = "Test Error";
    public static final Throwable TEST_ERROR = new Throwable(TEST_MESSAGE);

    private Logger() {
        // Never Called
    }

    public static <T> void logSuccess(@NonNull T item) {
        Timber.i(item.getClass().getName() + " successfully loaded with value: " + item.toString());
    }

    public static <T> void logSuccess(@NonNull List<T> items) {
        Timber.i("List of items successfully loaded with count: " + items.size());
    }

    public static void logError(@NonNull Throwable throwable) {
        Timber.e(throwable, "An exception occurred : %s", throwable.getMessage());
    }

}
