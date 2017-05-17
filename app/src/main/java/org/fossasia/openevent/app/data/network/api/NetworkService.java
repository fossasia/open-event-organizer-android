package org.fossasia.openevent.app.data.network.api;

import org.fossasia.openevent.app.utils.Constants;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {

    private static ILoginService loginService;

    static {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        loginService = retrofit.create(ILoginService.class);
    }

    public static ILoginService getLoginService() {
        return loginService;
    }

}
