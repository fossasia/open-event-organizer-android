package com.eventyay.organizer.common.di.module;

import com.eventyay.organizer.OrgaProvider;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.data.auth.AuthHolder;
import com.eventyay.organizer.data.copyright.Copyright;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.event.EventStatistics;
import com.eventyay.organizer.data.faq.Faq;
import com.eventyay.organizer.data.feedback.Feedback;
import com.eventyay.organizer.data.network.HostSelectionInterceptor;
import com.eventyay.organizer.data.notification.Notification;
import com.eventyay.organizer.data.order.Order;
import com.eventyay.organizer.data.order.OrderStatistics;
import com.eventyay.organizer.data.role.Role;
import com.eventyay.organizer.data.role.RoleInvite;
import com.eventyay.organizer.data.session.Session;
import com.eventyay.organizer.data.speaker.Speaker;
import com.eventyay.organizer.data.speakerscall.SpeakersCall;
import com.eventyay.organizer.data.sponsor.Sponsor;
import com.eventyay.organizer.data.ticket.OnSiteTicket;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.data.tracks.Track;
import com.eventyay.organizer.data.user.User;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory;

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

@Module(includes = ApiModule.class)
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class NetworkModule {

    private AuthHolder authHolder;

    @Provides
    @Singleton
    ObjectMapper providesObjectMapper() {
        return new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            // Handle constant breaking changes in API by not including null fields
            // TODO: Remove when API stabilizes and/or need to include null values is there
            .setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
    }

    @Provides
    Class[] providesMappedClasses() {
        return new Class[]{Event.class, Attendee.class, Ticket.class, User.class, EventStatistics.class,
            Faq.class, Copyright.class, Feedback.class, Track.class, Session.class, Sponsor.class,
            Speaker.class, SpeakersCall.class, Order.class, OrderStatistics.class, OnSiteTicket.class,
            RoleInvite.class, Role.class, Notification.class};
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
    Interceptor authenticator(AuthHolder authHolder) {
        this.authHolder = authHolder;
        return chain -> {
            Request original = chain.request();

            String authorization = authHolder.getAuthorization();
            if (authorization == null) {
                Timber.d("Someone tried to access resources without auth token. Maybe auth request?");
                return chain.proceed(original);
            }

            Request request = original.newBuilder()
                .header("Authorization", authorization)
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
            .baseUrl(authHolder.getBaseUrl())
            .build();
    }
}
