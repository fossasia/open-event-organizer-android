package com.eventyay.organizer.common.di.module;

import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.data.copyright.Copyright;
import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.db.DbFlowDatabaseChangeListener;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.faq.Faq;
import com.eventyay.organizer.data.role.RoleInvite;
import com.eventyay.organizer.data.session.Session;
import com.eventyay.organizer.data.speaker.Speaker;
import com.eventyay.organizer.data.speakerscall.SpeakersCall;
import com.eventyay.organizer.data.sponsor.Sponsor;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.data.tracks.Track;

import dagger.Module;
import dagger.Provides;

@Module
public class ChangeListenerModule {

    @Provides
    DatabaseChangeListener<Attendee> providesAttendeeChangeListener() {
        return new DbFlowDatabaseChangeListener<>(Attendee.class);
    }

    @Provides
    DatabaseChangeListener<Ticket> providesTicketChangeListener() {
        return new DbFlowDatabaseChangeListener<>(Ticket.class);
    }

    @Provides
    DatabaseChangeListener<Faq> providesFaqChangeListener() {
        return new DbFlowDatabaseChangeListener<>(Faq.class);
    }

    @Provides
    DatabaseChangeListener<Copyright> providesCopyrightChangeListener() {
        return new DbFlowDatabaseChangeListener<>(Copyright.class);
    }

    @Provides
    DatabaseChangeListener<Track> providesTrackChangeListener() {
        return new DbFlowDatabaseChangeListener<>(Track.class);
    }

    @Provides
    DatabaseChangeListener<Session> providesSessionChangeListener() {
        return new DbFlowDatabaseChangeListener<>(Session.class);
    }

    @Provides
    DatabaseChangeListener<Sponsor> providesSponsorChangeListener() {
        return new DbFlowDatabaseChangeListener<>(Sponsor.class);
    }

    @Provides
    DatabaseChangeListener<Speaker> providesSpeakerChangeListener() {
        return new DbFlowDatabaseChangeListener<>(Speaker.class);
    }

    @Provides
    DatabaseChangeListener<SpeakersCall> providesSpeakerCallChangeListener() {
        return new DbFlowDatabaseChangeListener<>(SpeakersCall.class);
    }

    @Provides
    DatabaseChangeListener<Event> providesEventChangeListener() {
        return new DbFlowDatabaseChangeListener<>(Event.class);
    }

    @Provides
    DatabaseChangeListener<RoleInvite> providesRoleListChangeListener() {
        return new DbFlowDatabaseChangeListener<>(RoleInvite.class);
    }
}
