package com.eventyay.organizer.data.auth;

import com.eventyay.organizer.data.auth.model.ChangePassword;
import com.eventyay.organizer.data.auth.model.ChangePasswordResponse;
import com.eventyay.organizer.data.auth.model.EmailRequest;
import com.eventyay.organizer.data.auth.model.EmailValidationResponse;
import com.eventyay.organizer.data.auth.model.Login;
import com.eventyay.organizer.data.auth.model.LoginResponse;
import com.eventyay.organizer.data.auth.model.EmailVerificationResponse;
import com.eventyay.organizer.data.auth.model.RequestToken;
import com.eventyay.organizer.data.auth.model.RequestTokenResponse;
import com.eventyay.organizer.data.auth.model.ResendVerificationMail;
import com.eventyay.organizer.data.auth.model.ResendVerificationMailResponse;
import com.eventyay.organizer.data.auth.model.SubmitEmailVerificationToken;
import com.eventyay.organizer.data.auth.model.SubmitToken;
import com.eventyay.organizer.data.auth.model.SubmitTokenResponse;
import com.eventyay.organizer.data.user.User;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

public interface AuthApi {

    @POST("users/checkEmail")
    Observable<EmailValidationResponse> checkEmail(@Body EmailRequest emailRequest);

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

    @POST("auth/resend-verification-email")
    Observable<ResendVerificationMailResponse> resendMail(@Body ResendVerificationMail resendVerificationMail);

    @POST("auth/verify-email")
    Observable<EmailVerificationResponse> verifyMail(@Body Map<String, SubmitEmailVerificationToken> submitMailVerificationToken);
}
