package org.fossasia.openevent.app.data.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.fossasia.openevent.app.data.db.configuration.DbFlowExclusionStrategy;
import org.fossasia.openevent.app.utils.Constants;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {

    private static EventService eventService;

    static {
        Gson gson =new GsonBuilder()
            .addDeserializationExclusionStrategy(new DbFlowExclusionStrategy())
            .create();

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
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
