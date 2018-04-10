package org.fossasia.openevent.app.data.auth;

import org.fossasia.openevent.app.data.auth.model.ChangePassword;
import org.fossasia.openevent.app.data.auth.model.ChangePasswordResponse;
import org.fossasia.openevent.app.data.auth.model.Login;
import org.fossasia.openevent.app.data.auth.model.LoginResponse;
import org.fossasia.openevent.app.data.auth.model.RequestToken;
import org.fossasia.openevent.app.data.auth.model.RequestTokenResponse;
import org.fossasia.openevent.app.data.auth.model.SubmitToken;
import org.fossasia.openevent.app.data.auth.model.SubmitTokenResponse;
import org.fossasia.openevent.app.data.user.User;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

public interface AuthApi {

    @POST("users")
    Observable<User> signUp(@Body User user);

    @POST("../auth/session")
    Observable<LoginResponse> login(@Body Login login);

    @POST("auth/reset-password")
    Observable<RequestTokenResponse> requestToken(@Body Map<String, RequestToken> reqToken);

    @PATCH("auth/reset-password")
    Observable<SubmitTokenResponse> submitToken(@Body Map<String, SubmitToken> subToken);

    @POST("auth/change-password")
    Observable<ChangePasswordResponse> changePassword(@Body Map<String, ChangePassword> changePassword);
}
