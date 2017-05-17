package org.fossasia.openevent.app.data.network.api;


import org.fossasia.openevent.app.data.models.Login;
import org.fossasia.openevent.app.data.models.LoginResponse;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ILoginService {

    @POST("login")
    Observable<LoginResponse> login(@Body Login login);

}
