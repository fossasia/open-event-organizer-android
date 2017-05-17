package org.fossasia.openevent.app.contract.model;

import org.fossasia.openevent.app.data.models.LoginResponse;

import io.reactivex.Observable;

public interface ILoginModel {

    boolean isLoggedIn();

    Observable<LoginResponse> login(String username,  String password);

}
