package org.fossasia.openevent.app.common.app.di.module;

import android.content.Context;
import android.support.annotation.NonNull;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.fossasia.openevent.app.common.data.contract.IUtilModel;
import org.fossasia.openevent.app.common.data.models.Attendee;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.models.Ticket;
import org.fossasia.openevent.app.common.data.models.User;
import org.fossasia.openevent.app.common.data.network.EventService;
import org.fossasia.openevent.app.common.data.network.HostSelectionInterceptor;
import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.common.utils.core.Utils;

import java.io.IOException;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import timber.log.Timber;

@Module(includes = { AndroidModule.class, DatabaseModule.class })
public class NetworkModule {

    @Provides
    @Singleton
    ObjectMapper providesObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Provides
    Class[] providesMappedClasses() {
        return new Class[]{Event.class, Attendee.class, Ticket.class, User.class};
    }

    @Provides
    @Singleton
    @Named("jsonapi")
    Converter.Factory providesJsonApiFactory(ObjectMapper objectMapper, Class[] mappedClasses) {
        return new JSONAPIConverterFactory(objectMapper, mappedClasses);
    }

    @Provides
    @Singleton
    @Named("jackson")
    Converter.Factory providesJacksonFactory(ObjectMapper objectMapper) {
        return JacksonConverterFactory.create(objectMapper);
    }

    @Provides
    @Singleton
    CallAdapter.Factory providesCallAdapterFactory() {
        return RxJava2CallAdapterFactory.create();
    }

    @Provides
    @Singleton
    HttpLoggingInterceptor loggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return interceptor;
    }

    @Provides
    @Singleton
    StethoInterceptor stethoInterceptor() {
        return new StethoInterceptor();
    }

    @Provides
    @Singleton
    Authenticator authenticator(IUtilModel utilModel) {
        return new Authenticator() {
            @Nullable
            @Override
            public Request authenticate(@NonNull Route route, @NonNull Response response) throws IOException {
                if (response.request().header("Authorization") != null) {
                    return null; // Give up, we've already failed to authenticate.
                }

                String token = utilModel.getToken();

                if (token == null) {
                    Timber.wtf("Someone tried to access authenticated resource without auth token");
                    return null;
                }

                return response.request().newBuilder()
                    .header("Authorization", Utils.formatToken(token))
                    .build();
            }
        };
    }

    @Provides
    @Singleton
    OkHttpClient providesOkHttpClient(
        HostSelectionInterceptor hostSelectionInterceptor,
        HttpLoggingInterceptor loggingInterceptor,
        StethoInterceptor stethoInterceptor,
        Authenticator authenticator
    ) {
        return new OkHttpClient.Builder()
            .addInterceptor(hostSelectionInterceptor)
            .addInterceptor(loggingInterceptor)
            .addNetworkInterceptor(stethoInterceptor)
            .authenticator(authenticator)
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
    Retrofit.Builder providesRetrofitBuilder(CallAdapter.Factory callAdapterFactory, @Named("jsonapi") Converter.Factory jsonApiConverter, @Named("jackson") Converter.Factory factory, OkHttpClient client) {
        return new Retrofit.Builder()
            .addCallAdapterFactory(callAdapterFactory)
            .addConverterFactory(jsonApiConverter)
            .addConverterFactory(factory)
            .client(client)
            .baseUrl(Constants.BASE_URL);
    }

    @Provides
    @Singleton
    EventService providesEventService(Retrofit.Builder retrofitBuilder) {
        return retrofitBuilder.build().create(EventService.class);
    }
}
