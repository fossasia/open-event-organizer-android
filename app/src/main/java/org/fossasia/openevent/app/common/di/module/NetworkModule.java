package org.fossasia.openevent.app.common.di.module;

import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.fossasia.openevent.app.data.db.configuration.DbFlowExclusionStrategy;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.data.network.HostSelectionInterceptor;
import org.fossasia.openevent.app.utils.Constants;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = { AndroidModule.class, DatabaseModule.class })
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
    HostSelectionInterceptor providesHostSelectionInterceptor() {
        return new HostSelectionInterceptor();
    }

    @Provides
    @Singleton
    OkHttpClient providesOkHttpClient(HostSelectionInterceptor interceptor) {
        return new OkHttpClient.Builder()
            .addInterceptor(interceptor)
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
    Retrofit.Builder providesRetrofitBuilder(Gson gson, OkHttpClient client) {
        return new Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .baseUrl(Constants.BASE_URL);
    }

    @Provides
    @Singleton
    EventService providesEventService(Retrofit.Builder builder) {
        return builder.build().create(EventService.class);
    }
}
