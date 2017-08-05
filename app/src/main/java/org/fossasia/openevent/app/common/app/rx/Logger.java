package org.fossasia.openevent.app.common.app.rx;

import java.util.List;

import timber.log.Timber;

public final class Logger {

    private Logger() {
        // Never Called
    }

    public static final String TEST_MESSAGE = "Test Error";
    public static final Throwable TEST_ERROR = new Throwable(TEST_MESSAGE);

    public static <T> void logSuccess(T item) {
        Timber.i(item.getClass().getName() + " successfully loaded with value: " + item.toString());
    }

    public static <T> void logSuccess(List<T> items) {
        Timber.i("List of items successfully loaded with value: " + items.toString());
    }

    public static void logError(Throwable throwable) {
        Timber.e(throwable, "An exception occurred : %s", throwable.getMessage());
        if (!throwable.equals(TEST_ERROR))
            throwable.printStackTrace();
    }

}
