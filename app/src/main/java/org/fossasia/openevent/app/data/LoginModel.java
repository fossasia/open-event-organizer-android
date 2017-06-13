package org.fossasia.openevent.app.data;

import org.fossasia.openevent.app.data.models.Login;
import org.fossasia.openevent.app.data.models.LoginResponse;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.data.contract.ILoginModel;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.utils.Constants;
import org.fossasia.openevent.app.utils.JWTUtils;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginModel implements ILoginModel {

    private IUtilModel utilModel;
    private EventService eventService;
    private String token;

    public LoginModel(IUtilModel utilModel, EventService eventService) {
        this.utilModel = utilModel;
        this.eventService = eventService;
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
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public boolean isLoggedIn() {
        if(token == null)
            token = utilModel.getToken();

        return token != null && !JWTUtils.isExpired(token);
    }

    @Override
    public void logout() {
        utilModel.saveToken(null);
    }

    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

}
