package org.fossasia.openevent.app.common.data.contract;

import io.reactivex.Completable;

public interface ILoginModel {

    Completable login(String username, String password);

    boolean isLoggedIn();

    Completable logout();

}
