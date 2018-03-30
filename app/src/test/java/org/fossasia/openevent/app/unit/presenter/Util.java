package org.fossasia.openevent.app.unit.presenter;

import org.fossasia.openevent.app.common.rx.Logger;

import io.reactivex.Observable;

final class Util {

    static final Observable ERROR_OBSERVABLE = Observable.error(Logger.TEST_ERROR);

    private Util() {
        // Never Called
    }
}
