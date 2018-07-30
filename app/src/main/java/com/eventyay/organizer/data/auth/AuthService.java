package com.eventyay.organizer.data.auth;

import com.eventyay.organizer.data.auth.model.ChangePassword;
import com.eventyay.organizer.data.auth.model.Login;
import com.eventyay.organizer.data.auth.model.RequestToken;
import com.eventyay.organizer.data.auth.model.SubmitToken;
import com.eventyay.organizer.data.user.User;

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
