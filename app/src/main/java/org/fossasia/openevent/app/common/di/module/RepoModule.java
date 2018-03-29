package org.fossasia.openevent.app.common.di.module;

import org.fossasia.openevent.app.data.db.DatabaseRepository;
import org.fossasia.openevent.app.data.db.IDatabaseRepository;
import org.fossasia.openevent.app.data.repository.AttendeeRepository;
import org.fossasia.openevent.app.data.repository.CopyrightRepository;
import org.fossasia.openevent.app.data.repository.EventRepository;
import org.fossasia.openevent.app.data.repository.FaqRepository;
import org.fossasia.openevent.app.data.repository.IAttendeeRepository;
import org.fossasia.openevent.app.data.repository.ICopyrightRepository;
import org.fossasia.openevent.app.data.repository.IEventRepository;
import org.fossasia.openevent.app.data.repository.IFaqRepository;
import org.fossasia.openevent.app.data.repository.ITicketRepository;
import org.fossasia.openevent.app.data.repository.TicketRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module(includes = ChangeListenerModule.class)
public abstract class RepoModule {

    @Binds
    @Singleton
    abstract IDatabaseRepository providesDatabaseRepository(DatabaseRepository databaseRepository);

    @Binds
    @Singleton
    abstract IEventRepository bindsEventRepository(EventRepository eventRepository);

    @Binds
    @Singleton
    abstract IAttendeeRepository providesAttendeeRepository(AttendeeRepository attendeeRepository);

    @Binds
    @Singleton
    abstract ITicketRepository bindsTicketRepository(TicketRepository ticketRepository);

    @Binds
    @Singleton
    abstract IFaqRepository bindsFAQRepository(FaqRepository faqRepository);

    @Binds
    @Singleton
    abstract ICopyrightRepository bindsCopyrightRepository(CopyrightRepository copyrightRepository);

}
