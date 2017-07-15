package org.fossasia.openevent.app.presenter;

import org.fossasia.openevent.app.common.rx.Logger;

import io.reactivex.Observable;

public class Util {
    public static final Observable ERROR_OBSERVABLE = Observable.error(Logger.TEST_ERROR);
}
