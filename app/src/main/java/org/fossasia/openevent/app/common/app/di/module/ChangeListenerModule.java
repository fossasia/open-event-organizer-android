package org.fossasia.openevent.app.common.app.di.module;

import org.fossasia.openevent.app.common.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.common.data.db.contract.IDatabaseChangeListener;
import org.fossasia.openevent.app.common.data.models.Attendee;
import org.fossasia.openevent.app.common.data.models.Copyright;
import org.fossasia.openevent.app.common.data.models.Faq;
import org.fossasia.openevent.app.common.data.models.Ticket;

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
