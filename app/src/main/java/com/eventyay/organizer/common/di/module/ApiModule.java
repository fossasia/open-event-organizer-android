package com.eventyay.organizer.common.di.module;

import com.eventyay.organizer.data.attendee.AttendeeApi;
import com.eventyay.organizer.data.auth.AuthApi;
import com.eventyay.organizer.data.copyright.CopyrightApi;
import com.eventyay.organizer.data.event.EventApi;
import com.eventyay.organizer.data.image.ImageUploadApi;
import com.eventyay.organizer.data.faq.FaqApi;
import com.eventyay.organizer.data.feedback.FeedbackApi;
import com.eventyay.organizer.data.order.OrderApi;
import com.eventyay.organizer.data.session.SessionApi;
import com.eventyay.organizer.data.speaker.SpeakerApi;
import com.eventyay.organizer.data.speakerscall.SpeakersCallApi;
import com.eventyay.organizer.data.sponsor.SponsorApi;
import com.eventyay.organizer.data.ticket.TicketApi;
import com.eventyay.organizer.data.tracks.TrackApi;
import com.eventyay.organizer.data.user.UserApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class ApiModule {

    @Provides
    @Singleton
    EventApi providesEventApi(Retrofit retrofit) {
        return retrofit.create(EventApi.class);
    }

    @Provides
    @Singleton
    AuthApi providesAuthApi(Retrofit retrofit) {
        return retrofit.create(AuthApi.class);
    }

    @Provides
    @Singleton
    AttendeeApi providesAttendeeApi(Retrofit retrofit) {
        return retrofit.create(AttendeeApi.class);
    }

    @Provides
    @Singleton
    TicketApi providesTicketApi(Retrofit retrofit) {
        return retrofit.create(TicketApi.class);
    }

    @Provides
    @Singleton
    CopyrightApi providesCopyrightApi(Retrofit retrofit) {
        return retrofit.create(CopyrightApi.class);
    }

    @Provides
    @Singleton
    FaqApi providesFaqApi(Retrofit retrofit) {
        return retrofit.create(FaqApi.class);
    }

    @Provides
    @Singleton
    FeedbackApi providesFeedbackApi(Retrofit retrofit) {
        return retrofit.create(FeedbackApi.class);
    }

    @Provides
    @Singleton
    TrackApi providesTrackApi(Retrofit retrofit) {
        return retrofit.create(TrackApi.class);
    }

    @Provides
    @Singleton
    UserApi providesUserApi(Retrofit retrofit) {
        return retrofit.create(UserApi.class);
    }

    @Provides
    @Singleton
    SessionApi providesSessionApi(Retrofit retrofit) {
        return retrofit.create(SessionApi.class);
    }

    @Provides
    @Singleton
    SponsorApi providesSponsorApi(Retrofit retrofit) {
        return retrofit.create(SponsorApi.class);
    }

    @Provides
    @Singleton
    SpeakerApi providesSpeakerApi(Retrofit retrofit) {
        return retrofit.create(SpeakerApi.class);
    }

    @Provides
    @Singleton
    SpeakersCallApi providesSpeakersCallApi(Retrofit retrofit) {
        return retrofit.create(SpeakersCallApi.class);
    }

    @Provides
    @Singleton
    OrderApi providesOrderApi(Retrofit retrofit) {
        return retrofit.create(OrderApi.class);
    }

    @Provides
    @Singleton
    ImageUploadApi providesImageUploadApi(Retrofit retrofit) {
        return retrofit.create(ImageUploadApi.class);
    }
}
