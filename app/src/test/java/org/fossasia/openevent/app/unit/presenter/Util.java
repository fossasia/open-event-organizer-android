package org.fossasia.openevent.app.unit.presenter;

import org.fossasia.openevent.app.common.app.rx.Logger;

import io.reactivex.Observable;

public final class Util {

    private Util() {
        // Never Called
    }

    public static final Observable ERROR_OBSERVABLE = Observable.error(Logger.TEST_ERROR);
}
