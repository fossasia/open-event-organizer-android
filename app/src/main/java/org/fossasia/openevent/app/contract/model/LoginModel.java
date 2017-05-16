package org.fossasia.openevent.app.contract.model;

import org.fossasia.openevent.app.data.models.LoginResponse;

import io.reactivex.Observable;

public interface LoginModel {

    Observable<LoginResponse> login(String username,  String password);

}
