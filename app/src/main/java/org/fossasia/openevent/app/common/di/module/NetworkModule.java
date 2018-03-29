package org.fossasia.openevent.app.common.di.module;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory;

import org.fossasia.openevent.app.OrgaProvider;
import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.data.IUtilModel;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Copyright;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.EventStatistics;
import org.fossasia.openevent.app.data.models.Faq;
import org.fossasia.openevent.app.data.models.Ticket;
import org.fossasia.openevent.app.data.models.User;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.data.network.HostSelectionInterceptor;
import org.fossasia.openevent.app.utils.Utils;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import timber.log.Timber;

@Module
public class NetworkModule {

    @Provides
    @Singleton
    ObjectMapper providesObjectMapper() {
        return new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    @Provides
    Class[] providesMappedClasses() {
        return new Class[]{Event.class, Attendee.class, Ticket.class, User.class, EventStatistics.class, Faq.class, Copyright.class};
    }

    @Provides
    @Singleton
    @Named("jsonapi")
    Converter.Factory providesJsonApiFactory(ObjectMapper objectMapper, Class... mappedClasses) {
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
    @Named("authenticator")
    Interceptor authenticator(IUtilModel utilModel) {
        return chain -> {
            Request original = chain.request();

            String token = utilModel.getToken();

            if (token == null) {
                Timber.wtf("Someone tried to access resources without auth token. Maybe auth request?");
                return chain.proceed(original);
            }

            Request request = original.newBuilder()
                .header("Authorization", Utils.formatToken(token))
                .method(original.method(), original.body())
                .build();

            return chain.proceed(request);
        };
    }

    @Provides
    @Singleton
    Cache providesCache() {
        int cacheSize = 10 * 1024 * 1024; // 10 MB

        return new Cache(OrgaProvider.context.getCacheDir(), cacheSize);
    }

    @Provides
    @Singleton
    OkHttpClient providesOkHttpClient(
        HostSelectionInterceptor hostSelectionInterceptor,
        HttpLoggingInterceptor loggingInterceptor,
        StethoInterceptor stethoInterceptor,
        @Named("authenticator") Interceptor authenticator,
        Cache cache
    ) {
        return new OkHttpClient.Builder()
            .addInterceptor(hostSelectionInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authenticator)
            .addNetworkInterceptor(stethoInterceptor)
            .cache(cache)
            .build();
    }

    @Provides
    @Singleton
    Retrofit providesRetrofitBuilder(CallAdapter.Factory callAdapterFactory,
                                     @Named("jsonapi") Converter.Factory jsonApiConverter,
                                     @Named("jackson") Converter.Factory factory, OkHttpClient client) {
        return new Retrofit.Builder()
            .addCallAdapterFactory(callAdapterFactory)
            .addConverterFactory(jsonApiConverter)
            .addConverterFactory(factory)
            .client(client)
            .baseUrl(Constants.BASE_URL)
            .build();
    }

    @Provides
    @Singleton
    EventService providesEventService(Retrofit retrofit) {
        return retrofit.create(EventService.class);
    }
}
