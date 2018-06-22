package org.fossasia.openevent.app.data.auth;

import android.content.Context;
import android.os.Build;

import org.fossasia.openevent.app.data.auth.model.ChangePassword;
import org.fossasia.openevent.app.data.auth.model.CustomObjectWrapper;
import org.fossasia.openevent.app.data.auth.model.Login;
import org.fossasia.openevent.app.data.auth.model.RequestToken;
import org.fossasia.openevent.app.data.auth.model.SubmitToken;
import org.fossasia.openevent.app.data.user.User;
import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.data.Repository;
import org.fossasia.openevent.app.utils.EncryptionUtils;

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
    Context context;

    @Inject
    public AuthServiceImpl(AuthHolder authHolder, Repository repository, AuthApi authApi) {
        this.authHolder = authHolder;
        this.repository = repository;
        this.authApi = authApi;
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    authHolder.saveEncryptedEmail(EncryptionUtils.encryptString(context, login.getEmail()));
                    authHolder.saveEncryptedPassword(EncryptionUtils.encryptString(context, login.getPassword()));
                } else {
                    authHolder.saveEncryptedEmail(login.getEmail());
                    authHolder.saveEncryptedPassword(login.getPassword());
                }
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
