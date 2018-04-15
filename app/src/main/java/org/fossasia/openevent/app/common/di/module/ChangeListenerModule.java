package org.fossasia.openevent.app.common.di.module;

import org.fossasia.openevent.app.data.attendee.Attendee;
import org.fossasia.openevent.app.data.copyright.Copyright;
import org.fossasia.openevent.app.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.data.db.DbFlowDatabaseChangeListener;
import org.fossasia.openevent.app.data.faq.Faq;
import org.fossasia.openevent.app.data.session.Session;
import org.fossasia.openevent.app.data.ticket.Ticket;
import org.fossasia.openevent.app.data.tracks.Track;

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
}
