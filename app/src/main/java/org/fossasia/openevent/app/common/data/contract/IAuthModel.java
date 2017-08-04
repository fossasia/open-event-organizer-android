package org.fossasia.openevent.app.common.data.contract;

import org.fossasia.openevent.app.common.data.models.User;
import org.fossasia.openevent.app.common.data.models.dto.Login;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface IAuthModel {

    Completable login(Login login);

    Observable<User> signUp(User newUser);

    boolean isLoggedIn();

    Completable logout();

}
