package org.fossasia.openevent.app.data;

import org.fossasia.openevent.app.data.models.ChangePassword;
import org.fossasia.openevent.app.data.models.RequestToken;
import org.fossasia.openevent.app.data.models.SubmitToken;
import org.fossasia.openevent.app.data.models.User;
import org.fossasia.openevent.app.data.models.dto.Login;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface IAuthModel {

    Completable login(Login login);

    Observable<User> signUp(User newUser);

    boolean isLoggedIn();

    Completable logout();

    Completable requestToken(RequestToken data);

    Completable submitToken(SubmitToken tokenData);

    Completable changePassword(ChangePassword changePassword);
}
