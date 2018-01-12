package org.fossasia.openevent.app.common.data;

import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.common.app.ContextManager;
import org.fossasia.openevent.app.common.data.contract.IAuthModel;
import org.fossasia.openevent.app.common.data.contract.ISharedPreferenceModel;
import org.fossasia.openevent.app.common.data.contract.IUtilModel;
import org.fossasia.openevent.app.common.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.common.data.models.CustomObjectWrapper;
import org.fossasia.openevent.app.common.data.models.RequestToken;
import org.fossasia.openevent.app.common.data.models.SubmitToken;
import org.fossasia.openevent.app.common.data.models.User;
import org.fossasia.openevent.app.common.data.models.dto.Login;
import org.fossasia.openevent.app.common.data.network.EventService;
import org.fossasia.openevent.app.common.utils.core.JWTUtils;
import org.fossasia.openevent.app.module.main.MainActivity;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AuthModel implements IAuthModel {

    private static final User EMPTY = new User();

    private final ISharedPreferenceModel sharedPreferenceModel;
    private final IUtilModel utilModel;
    private final EventService eventService;
    private final IDatabaseRepository databaseRepository;

    @Inject
    public AuthModel(IUtilModel utilModel, ISharedPreferenceModel sharedPreferenceModel,
                     EventService eventService, IDatabaseRepository databaseRepository) {
        this.utilModel = utilModel;
        this.sharedPreferenceModel = sharedPreferenceModel;
        this.eventService = eventService;
        this.databaseRepository = databaseRepository;
    }

    @Override
    public Completable login(Login login) {
        if (isLoggedIn())
            return Completable.complete();

        if (!utilModel.isConnected())
            return Completable.error(new Throwable(Constants.NO_NETWORK));

        return eventService
            .login(login)
            .flatMapSingle(loginResponse -> {
                String token = loginResponse.getAccessToken();
                utilModel.saveToken(token);
                sharedPreferenceModel.addStringSetElement(Constants.SHARED_PREFS_SAVED_EMAIL, login.getEmail());

                return isPreviousUser(token);
            })
            .flatMapCompletable(isPrevious -> {
                if (!isPrevious)
                    return utilModel.deleteDatabase();

                return Completable.complete();
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<User> signUp(User newUser) {
        if (!utilModel.isConnected())
            return Observable.error(new Throwable(Constants.NO_NETWORK));

        return eventService
            .signUp(newUser)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @VisibleForTesting
    public Single<Boolean> isPreviousUser(String token) {
        return databaseRepository.getAllItems(User.class)
            .first(EMPTY)
            .map(user -> !user.equals(EMPTY) && user.getId() == JWTUtils.getIdentity(token));
    }

    @Override
    public boolean isLoggedIn() {
        String token = utilModel.getToken();

        return token != null && !JWTUtils.isExpired(token);
    }

    @Override
    public Completable logout() {
        return Completable.fromAction(
            () -> {
                utilModel.saveToken(null);
                sharedPreferenceModel.setLong(MainActivity.EVENT_KEY, -1);
                ContextManager.setSelectedEvent(null);
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable requestToken(RequestToken reqToken) {
        if (!utilModel.isConnected())
            return Completable.error(new Throwable(Constants.NO_NETWORK));

        return eventService
            .requestToken(CustomObjectWrapper.withLabel("data", reqToken))
            .flatMapCompletable(
                var -> Completable.complete())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable submitToken(SubmitToken tokenData) {
        if (!utilModel.isConnected())
            return Completable.error(new Throwable(Constants.NO_NETWORK));

        return eventService
            .submitToken(CustomObjectWrapper.withLabel("data", tokenData))
            .flatMapCompletable(
                var -> Completable.complete())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }
}
