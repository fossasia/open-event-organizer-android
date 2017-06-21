package org.fossasia.openevent.app.data;

import org.fossasia.openevent.app.data.contract.ILoginModel;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.data.models.Login;
import org.fossasia.openevent.app.data.models.LoginResponse;
import org.fossasia.openevent.app.data.models.User;
import org.fossasia.openevent.app.data.models.UserDetail;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.utils.Constants;
import org.fossasia.openevent.app.utils.JWTUtils;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class LoginModel implements ILoginModel {

    private IUtilModel utilModel;
    private EventService eventService;
    private IDatabaseRepository databaseRepository;
    private String token;

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
                .doOnNext(loginResponse -> {
                    token = loginResponse.getAccessToken();
                    utilModel.saveToken(token);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public boolean isLoggedIn() {
        if(token == null)
            token = utilModel.getToken();

        boolean loggedIn = token != null && !JWTUtils.isExpired(token);

        if(!loggedIn) {
            //noinspection unchecked
            databaseRepository.deleteAll(User.class, UserDetail.class)
                .subscribeOn(Schedulers.io())
                .subscribe();
        }

        return loggedIn;
    }

    @Override
    public Completable logout() {
        return Completable.fromAction(() -> {
            token = null;
            utilModel.saveToken(null);
            // Bug in foreign key relation, have to manually delete UserDetail
            //noinspection unchecked
            databaseRepository.deleteAll(User.class, UserDetail.class)
                .subscribe(() -> Timber.d("Deleted User"),
                    Throwable::printStackTrace);
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

}
