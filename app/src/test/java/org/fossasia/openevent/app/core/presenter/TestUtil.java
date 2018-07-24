package org.fossasia.openevent.app.core.presenter;

import org.fossasia.openevent.app.common.rx.Logger;

import io.reactivex.Observable;

public final class TestUtil {

    public static final Observable ERROR_OBSERVABLE = Observable.error(Logger.TEST_ERROR);

    private TestUtil() {
        // Never Called
    }
}
