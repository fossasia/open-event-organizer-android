package org.fossasia.openevent.app.data.network.api;

import org.fossasia.openevent.app.utils.Constants;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {

    private static EventService eventService;

    static {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        eventService = retrofit.create(EventService.class);
    }

    public static String formatToken(String token) {
        return String.format("JWT %s", token);
    }

    public static EventService getEventService() {
        return eventService;
    }

}
