package com.eventyay.organizer.core.stripeauth;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface OAuthServerInterface {

    /**
     * The call to request a token
     */
    @FormUrlEncoded
    @POST("oauth/token")
    Call<OAuthToken> requestTokenForm(
        @Field("client_secret")String client_secret,
        @Field("code")String code,
        @Field("grant_type")String grant_type);
}
