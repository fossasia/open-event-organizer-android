package org.fossasia.openevent.app.data;

import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.data.contract.ILoginModel;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.data.models.Login;
import org.fossasia.openevent.app.data.models.User;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.main.MainActivity;
import org.fossasia.openevent.app.utils.Constants;
import org.fossasia.openevent.app.utils.JWTUtils;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginModel implements ILoginModel {

    private static final User EMPTY = new User();

    private final IUtilModel utilModel;
    private final EventService eventService;
    private final IDatabaseRepository databaseRepository;

    @Inject
    public LoginModel(IUtilModel utilModel, EventService eventService, IDatabaseRepository databaseRepository) {
        this.utilModel = utilModel;
        this.eventService = eventService;
        this.databaseRepository = databaseRepository;
    }

    @Override
    public Completable login(String username, String password) {
        if(isLoggedIn())
            return Completable.complete();

        if(!utilModel.isConnected())
            return Completable.error(new RuntimeException(Constants.NO_NETWORK));

        return eventService
            .login(new Login(username, password))
            .flatMapSingle(loginResponse -> {
                String token = loginResponse.getAccessToken();
                utilModel.saveToken(token);
                utilModel.addStringSetElement(Constants.SHARED_PREFS_SAVED_EMAIL, username);

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

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
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
                utilModel.setLong(MainActivity.EVENT_KEY, -1);
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

}
