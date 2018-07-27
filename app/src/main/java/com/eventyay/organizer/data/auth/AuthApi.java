package com.eventyay.organizer.data.auth;

import com.eventyay.organizer.data.auth.model.ChangePassword;
import com.eventyay.organizer.data.auth.model.ChangePasswordResponse;
import com.eventyay.organizer.data.auth.model.Login;
import com.eventyay.organizer.data.auth.model.LoginResponse;
import com.eventyay.organizer.data.auth.model.RequestToken;
import com.eventyay.organizer.data.auth.model.RequestTokenResponse;
import com.eventyay.organizer.data.auth.model.SubmitToken;
import com.eventyay.organizer.data.auth.model.SubmitTokenResponse;
import com.eventyay.organizer.data.user.User;

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
