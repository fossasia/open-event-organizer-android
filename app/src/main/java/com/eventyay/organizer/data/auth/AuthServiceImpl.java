package com.eventyay.organizer.data.auth;

import com.eventyay.organizer.data.auth.model.ChangePassword;
import com.eventyay.organizer.data.auth.model.CustomObjectWrapper;
import com.eventyay.organizer.data.auth.model.EmailRequest;
import com.eventyay.organizer.data.auth.model.EmailValidationResponse;
import com.eventyay.organizer.data.auth.model.Login;
import com.eventyay.organizer.data.auth.model.RequestToken;
import com.eventyay.organizer.data.auth.model.SubmitToken;
import com.eventyay.organizer.data.user.User;
import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.data.Repository;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AuthServiceImpl implements AuthService {

    private static final User EMPTY = new User();

    private final AuthHolder authHolder;
    private final Repository repository;
    private final AuthApi authApi;

    @Inject
    public AuthServiceImpl(AuthHolder authHolder, Repository repository, AuthApi authApi) {
        this.authHolder = authHolder;
        this.repository = repository;
        this.authApi = authApi;
    }

    @Override
    public Observable<EmailValidationResponse> checkEmailRegistered(EmailRequest emailRequest) {
        if (!repository.isConnected())
            return Observable.error(new Throwable(Constants.NO_NETWORK));

        return authApi
            .checkEmail(emailRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable login(Login login) {
        if (isLoggedIn())
            return Completable.complete();

        if (!repository.isConnected())
            return Completable.error(new Throwable(Constants.NO_NETWORK));

        return authApi
            .login(login)
            .flatMapSingle(loginResponse -> {
                String token = loginResponse.getAccessToken();
                authHolder.login(token);
                authHolder.saveEmail(login.getEmail());

                return isPreviousUser();
            })
            .flatMapCompletable(isPrevious -> {
                if (!isPrevious)
                    return repository.deleteDatabase();

                return Completable.complete();
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<User> signUp(User newUser) {
        if (!repository.isConnected())
            return Observable.error(new Throwable(Constants.NO_NETWORK));

        return authApi
            .signUp(newUser)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    private Single<Boolean> isPreviousUser() {
        return repository
            .getAllItems(User.class)
            .first(EMPTY)
            .map(user -> !user.equals(EMPTY) && authHolder.isUser(user));
    }

    @Override
    public boolean isLoggedIn() {
        return authHolder.isLoggedIn();
    }

    @Override
    public Completable logout() {
        return Completable.fromAction(
            authHolder::logout)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable requestToken(RequestToken reqToken) {
        if (!repository.isConnected())
            return Completable.error(new Throwable(Constants.NO_NETWORK));

        return authApi
            .requestToken(CustomObjectWrapper.withLabel("data", reqToken))
            .flatMapCompletable(
                var -> Completable.complete())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable submitToken(SubmitToken tokenData) {
        if (!repository.isConnected())
            return Completable.error(new Throwable(Constants.NO_NETWORK));

        return authApi
            .submitToken(CustomObjectWrapper.withLabel("data", tokenData))
            .flatMapCompletable(
                var -> Completable.complete())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable changePassword(ChangePassword changePassword) {
        if (!repository.isConnected())
            return Completable.error(new Throwable(Constants.NO_NETWORK));

        return authApi
            .changePassword(CustomObjectWrapper.withLabel("data", changePassword))
            .flatMapCompletable(
                var -> Completable.complete())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }
}
