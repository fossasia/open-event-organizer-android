package com.eventyay.organizer.common.di.module;

import com.eventyay.organizer.data.attendee.AttendeeRepository;
import com.eventyay.organizer.data.attendee.AttendeeRepositoryImpl;
import com.eventyay.organizer.data.copyright.CopyrightRepository;
import com.eventyay.organizer.data.copyright.CopyrightRepositoryImpl;
import com.eventyay.organizer.data.db.DatabaseRepository;
import com.eventyay.organizer.data.db.DbFlowDatabaseRepository;
import com.eventyay.organizer.data.event.EventRepository;
import com.eventyay.organizer.data.event.EventRepositoryImpl;
import com.eventyay.organizer.data.faq.FaqRepository;
import com.eventyay.organizer.data.faq.FaqRepositoryImpl;
import com.eventyay.organizer.data.feedback.FeedbackRepository;
import com.eventyay.organizer.data.feedback.FeedbackRepositoryImpl;
import com.eventyay.organizer.data.order.OrderRepository;
import com.eventyay.organizer.data.order.OrderRepositoryImpl;
import com.eventyay.organizer.data.session.SessionRepository;
import com.eventyay.organizer.data.session.SessionRepositoryImpl;
import com.eventyay.organizer.data.speaker.SpeakerRepository;
import com.eventyay.organizer.data.speaker.SpeakerRepositoryImpl;
import com.eventyay.organizer.data.speakerscall.SpeakersCallRepository;
import com.eventyay.organizer.data.speakerscall.SpeakersCallRepositoryImpl;
import com.eventyay.organizer.data.sponsor.SponsorRepository;
import com.eventyay.organizer.data.sponsor.SponsorRepositoryImpl;
import com.eventyay.organizer.data.ticket.TicketRepository;
import com.eventyay.organizer.data.ticket.TicketRepositoryImpl;
import com.eventyay.organizer.data.tracks.TrackRepository;
import com.eventyay.organizer.data.tracks.TrackRepositoryImpl;
import com.eventyay.organizer.data.user.UserRepository;
import com.eventyay.organizer.data.user.UserRepositoryImpl;

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

    @Binds
    @Singleton
    abstract SpeakersCallRepository bindsSpeakersCallRepository(SpeakersCallRepositoryImpl speakersCallRepository);

    @Binds
    @Singleton
    abstract OrderRepository bindsOrderRepository(OrderRepositoryImpl orderRepository);

}
