package org.fossasia.openevent.app.common.di.module;

import org.fossasia.openevent.app.data.AuthModel;
import org.fossasia.openevent.app.data.Bus;
import org.fossasia.openevent.app.data.SharedPreferenceModel;
import org.fossasia.openevent.app.data.UtilModel;
import org.fossasia.openevent.app.data.IAuthModel;
import org.fossasia.openevent.app.data.IBus;
import org.fossasia.openevent.app.data.ISharedPreferenceModel;
import org.fossasia.openevent.app.data.IUtilModel;
import org.fossasia.openevent.app.data.repository.AttendeeRepository;
import org.fossasia.openevent.app.data.repository.CopyrightRepository;
import org.fossasia.openevent.app.data.repository.EventRepository;
import org.fossasia.openevent.app.data.repository.FaqRepository;
import org.fossasia.openevent.app.data.repository.TicketRepository;
import org.fossasia.openevent.app.data.repository.IAttendeeRepository;
import org.fossasia.openevent.app.data.repository.ICopyrightRepository;
import org.fossasia.openevent.app.data.repository.IEventRepository;
import org.fossasia.openevent.app.data.repository.IFaqRepository;
import org.fossasia.openevent.app.data.repository.ITicketRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class DataModule {

    @Binds
    @Singleton
    abstract IBus bindsBus(Bus bus);

    @Binds
    @Singleton
    abstract IUtilModel bindsUtilModel(UtilModel utilModel);

    @Binds
    @Singleton
    abstract ISharedPreferenceModel bindsSharedPreferenceModel(SharedPreferenceModel sharedPreferenceModel);

    @Binds
    @Singleton
    abstract IAuthModel bindsLoginModule(AuthModel authModel);

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
