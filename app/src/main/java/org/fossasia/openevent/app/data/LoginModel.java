package org.fossasia.openevent.app.data;

import org.fossasia.openevent.app.data.models.Login;
import org.fossasia.openevent.app.data.models.LoginResponse;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.data.network.NetworkService;
import org.fossasia.openevent.app.data.contract.ILoginModel;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.utils.Constants;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginModel implements ILoginModel {

    private IUtilModel utilModel;
    private EventService eventService;

    public LoginModel(IUtilModel utilModel) {
        this.utilModel = utilModel;
    }

    @Override
    public Observable<LoginResponse> login(String username, String password) {
        if(utilModel.isLoggedIn()) {
            return Observable.just(new LoginResponse(utilModel.getToken()));
        }

        if(!utilModel.isConnected()) {
            return Observable.error(new RuntimeException(Constants.NO_NETWORK));
        }

        if(eventService == null)
            eventService = NetworkService.getEventService();

        return eventService
                .login(new Login(username, password))
                .doOnNext(loginResponse -> utilModel.saveToken(loginResponse.getAccessToken()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

}
