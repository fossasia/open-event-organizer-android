package com.eventyay.organizer.core.presenter;

import com.eventyay.organizer.common.rx.Logger;
import io.reactivex.Observable;

public final class TestUtil {

    public static final Observable ERROR_OBSERVABLE = Observable.error(Logger.TEST_ERROR);

    private TestUtil() {
        // Never Called
    }
}
