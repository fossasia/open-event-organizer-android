package org.fossasia.openevent.app.data.contract;

import org.fossasia.openevent.app.data.models.LoginResponse;

import io.reactivex.Observable;

public interface ILoginModel {

    Observable<LoginResponse> login(String username, String password);

    boolean isLoggedIn();

    void logout();

}
