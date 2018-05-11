package org.fossasia.openevent.app.common.di.module;

import org.fossasia.openevent.app.data.attendee.AttendeeRepository;
import org.fossasia.openevent.app.data.attendee.AttendeeRepositoryImpl;
import org.fossasia.openevent.app.data.copyright.CopyrightRepository;
import org.fossasia.openevent.app.data.copyright.CopyrightRepositoryImpl;
import org.fossasia.openevent.app.data.db.DatabaseRepository;
import org.fossasia.openevent.app.data.db.DbFlowDatabaseRepository;
import org.fossasia.openevent.app.data.event.EventRepository;
import org.fossasia.openevent.app.data.event.EventRepositoryImpl;
import org.fossasia.openevent.app.data.faq.FaqRepository;
import org.fossasia.openevent.app.data.faq.FaqRepositoryImpl;
import org.fossasia.openevent.app.data.feedback.FeedbackRepository;
import org.fossasia.openevent.app.data.feedback.FeedbackRepositoryImpl;
import org.fossasia.openevent.app.data.session.SessionRepository;
import org.fossasia.openevent.app.data.session.SessionRepositoryImpl;
import org.fossasia.openevent.app.data.speaker.SpeakerRepository;
import org.fossasia.openevent.app.data.speaker.SpeakerRepositoryImpl;
import org.fossasia.openevent.app.data.sponsor.SponsorRepository;
import org.fossasia.openevent.app.data.sponsor.SponsorRepositoryImpl;
import org.fossasia.openevent.app.data.ticket.TicketRepository;
import org.fossasia.openevent.app.data.ticket.TicketRepositoryImpl;
import org.fossasia.openevent.app.data.tracks.TrackRepository;
import org.fossasia.openevent.app.data.tracks.TrackRepositoryImpl;
import org.fossasia.openevent.app.data.user.UserRepository;
import org.fossasia.openevent.app.data.user.UserRepositoryImpl;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module(includes = ChangeListenerModule.class)
public abstract class RepoModule {

    @Binds
    @Singleton
    abstract DatabaseRepository providesDatabaseRepository(DbFlowDatabaseRepository databaseRepository);

    @Binds
    @Singleton
    abstract EventRepository bindsEventRepository(EventRepositoryImpl eventRepository);

    @Binds
    @Singleton
    abstract AttendeeRepository providesAttendeeRepository(AttendeeRepositoryImpl attendeeRepository);

    @Binds
    @Singleton
    abstract TicketRepository bindsTicketRepository(TicketRepositoryImpl ticketRepository);

    @Binds
    @Singleton
    abstract FaqRepository bindsFAQRepository(FaqRepositoryImpl faqRepository);

    @Binds
    @Singleton
    abstract CopyrightRepository bindsCopyrightRepository(CopyrightRepositoryImpl copyrightRepository);

    @Binds
    @Singleton
    abstract FeedbackRepository bindsFeedbackRepository(FeedbackRepositoryImpl feedbackRepository);

    @Binds
    @Singleton
    abstract TrackRepository bindsTrackRepository(TrackRepositoryImpl trackRepositoryImpl);

    @Binds
    @Singleton
    abstract UserRepository bindsUserRepository(UserRepositoryImpl userRepository);

    @Binds
    @Singleton
    abstract SessionRepository bindsSessionRepository(SessionRepositoryImpl sessionRepository);

    @Binds
    @Singleton
    abstract SponsorRepository bindsSponsorRepository(SponsorRepositoryImpl sponsorRepositoryImpl);

    @Binds
    @Singleton
    abstract SpeakerRepository bindsSpeakerRepository(SpeakerRepositoryImpl speakerRepository);
}
