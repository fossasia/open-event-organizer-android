package com.eventyay.organizer.core.stripeauth;

import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBuilder {

    public static final String BASE_URL = "https://connect.stripe.com/";

    /**
     * A basic client to make unauthenticated calls
     * @param context
     * @return OAuthServerInterface instance
     */
    public static OAuthServerInterface getClient(Context context) {
        //Using Default HttpClient
        Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build();
        OAuthServerInterface webServer = retrofit.create(OAuthServerInterface.class);
        return webServer;
    }
}
