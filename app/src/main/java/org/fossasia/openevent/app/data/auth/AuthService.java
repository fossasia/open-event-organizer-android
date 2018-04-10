package org.fossasia.openevent.app.data.auth;

import org.fossasia.openevent.app.data.auth.model.ChangePassword;
import org.fossasia.openevent.app.data.auth.model.Login;
import org.fossasia.openevent.app.data.auth.model.RequestToken;
import org.fossasia.openevent.app.data.auth.model.SubmitToken;
import org.fossasia.openevent.app.data.user.User;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface AuthService {

    Completable login(Login login);

    Observable<User> signUp(User newUser);

    boolean isLoggedIn();

    Completable logout();

    Completable requestToken(RequestToken data);

    Completable submitToken(SubmitToken tokenData);

    Completable changePassword(ChangePassword changePassword);
}
