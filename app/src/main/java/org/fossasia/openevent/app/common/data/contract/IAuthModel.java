package org.fossasia.openevent.app.common.data.contract;

import org.fossasia.openevent.app.common.data.models.User;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface IAuthModel {

    Completable login(String username, String password);

    Observable<User> signUp(User newUser);

    boolean isLoggedIn();

    Completable logout();

}
