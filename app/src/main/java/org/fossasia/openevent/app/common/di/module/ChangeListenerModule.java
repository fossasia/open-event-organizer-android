package org.fossasia.openevent.app.common.di.module;

import org.fossasia.openevent.app.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.data.db.IDatabaseChangeListener;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Copyright;
import org.fossasia.openevent.app.data.models.Faq;
import org.fossasia.openevent.app.data.models.Ticket;

import dagger.Module;
import dagger.Provides;

@Module
public class ChangeListenerModule {

    @Provides
    IDatabaseChangeListener<Attendee> providesAttendeeChangeListener() {
        return new DatabaseChangeListener<>(Attendee.class);
    }

    @Provides
    IDatabaseChangeListener<Ticket> providesTicketChangeListener() {
        return new DatabaseChangeListener<>(Ticket.class);
    }

    @Provides
    IDatabaseChangeListener<Faq> providesFaqChangeListener() {
        return new DatabaseChangeListener<>(Faq.class);
    }

    @Provides
    IDatabaseChangeListener<Copyright> providesCopyrightChangeListener() {
        return new DatabaseChangeListener<>(Copyright.class);
    }
}
