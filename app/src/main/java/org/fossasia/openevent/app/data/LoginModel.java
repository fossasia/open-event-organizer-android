package org.fossasia.openevent.app.data;

import org.fossasia.openevent.app.data.contract.ILoginModel;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.data.models.Login;
import org.fossasia.openevent.app.data.models.LoginResponse;
import org.fossasia.openevent.app.data.models.User;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.utils.Constants;
import org.fossasia.openevent.app.utils.JWTUtils;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginModel implements ILoginModel {

    private IUtilModel utilModel;
    private EventService eventService;
    private IDatabaseRepository databaseRepository;

    @Inject
    public LoginModel(IUtilModel utilModel, EventService eventService, IDatabaseRepository databaseRepository) {
        this.utilModel = utilModel;
        this.eventService = eventService;
        this.databaseRepository = databaseRepository;
    }

    @Override
    public Observable<LoginResponse> login(String username, String password) {
        if(isLoggedIn()) {
            return Observable.just(new LoginResponse(utilModel.getToken()));
        }

        if(!utilModel.isConnected()) {
            return Observable.error(new RuntimeException(Constants.NO_NETWORK));
        }

        return eventService
                .login(new Login(username, password))
                .doOnNext(loginResponse -> utilModel.saveToken(loginResponse.getAccessToken()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() ->
                    databaseRepository.getAllItems(User.class)
                    .any(user -> !user.getEmail().equals(username))
                    .filter(differentUser -> differentUser)
                    .subscribeOn(Schedulers.io())
                    .subscribe(differentUser ->
                        utilModel.deleteDatabase()
                            .subscribe()
                    ));
    }

    @Override
    public boolean isLoggedIn() {
        String token = utilModel.getToken();

        return token != null && !JWTUtils.isExpired(token);
    }

    @Override
    public Completable logout() {
        return Completable.fromAction(() -> utilModel.saveToken(null))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

}
