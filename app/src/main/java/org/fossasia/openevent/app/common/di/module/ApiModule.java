package org.fossasia.openevent.app.common.di.module;

import org.fossasia.openevent.app.data.attendee.AttendeeApi;
import org.fossasia.openevent.app.data.auth.AuthApi;
import org.fossasia.openevent.app.data.copyright.CopyrightApi;
import org.fossasia.openevent.app.data.event.EventApi;
import org.fossasia.openevent.app.data.faq.FaqApi;
import org.fossasia.openevent.app.data.feedback.FeedbackApi;
import org.fossasia.openevent.app.data.session.SessionApi;
import org.fossasia.openevent.app.data.speakerscall.SpeakersCallApi;
import org.fossasia.openevent.app.data.speaker.SpeakerApi;
import org.fossasia.openevent.app.data.sponsor.SponsorApi;
import org.fossasia.openevent.app.data.ticket.TicketApi;
import org.fossasia.openevent.app.data.tracks.TrackApi;
import org.fossasia.openevent.app.data.user.UserApi;

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
}
