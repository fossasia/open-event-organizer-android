package org.fossasia.openevent.app.common.di.module;

import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.fossasia.openevent.app.data.LoginModel;
import org.fossasia.openevent.app.data.contract.ILoginModel;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.db.configuration.DbFlowExclusionStrategy;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.utils.Constants;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {

    @Provides
    @Singleton
    ExclusionStrategy providesExclusionStrategy() {
        return new DbFlowExclusionStrategy();
    }

    @Provides
    @Singleton
    Gson providesGson(ExclusionStrategy exclusionStrategy) {
        return new GsonBuilder()
            .addDeserializationExclusionStrategy(exclusionStrategy)
            .create();
    }

    @Provides
    @Singleton
    OkHttpClient providesOkHttpClient() {
        return new OkHttpClient.Builder()
            .addNetworkInterceptor(new StethoInterceptor())
            .build();
    }

    @Provides
    @Singleton
    Picasso providesPicasso(Context context, OkHttpClient client) {
        return new Picasso.Builder(context)
            .downloader(new OkHttp3Downloader(client))
            .build();
    }

    @Provides
    @Singleton
    Retrofit providesRetrofit(Gson gson, OkHttpClient client) {
        return new Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build();
    }

    @Provides
    @Singleton
    EventService providesEventService(Retrofit retrofit) {
        return retrofit.create(EventService.class);
    }

    @Provides
    @Singleton
    ILoginModel providesLoginModel(IUtilModel utilModel, EventService eventService) {
        return new LoginModel(utilModel, eventService);
    }
}
