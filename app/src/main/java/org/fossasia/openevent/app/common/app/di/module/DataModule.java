package org.fossasia.openevent.app.common.app.di.module;

import org.fossasia.openevent.app.common.data.AuthModel;
import org.fossasia.openevent.app.common.data.Bus;
import org.fossasia.openevent.app.common.data.SharedPreferenceModel;
import org.fossasia.openevent.app.common.data.UtilModel;
import org.fossasia.openevent.app.common.data.contract.IAuthModel;
import org.fossasia.openevent.app.common.data.contract.IBus;
import org.fossasia.openevent.app.common.data.contract.ISharedPreferenceModel;
import org.fossasia.openevent.app.common.data.contract.IUtilModel;
import org.fossasia.openevent.app.common.data.repository.AttendeeRepository;
import org.fossasia.openevent.app.common.data.repository.EventRepository;
import org.fossasia.openevent.app.common.data.repository.FaqRepository;
import org.fossasia.openevent.app.common.data.repository.TicketRepository;
import org.fossasia.openevent.app.common.data.repository.contract.IAttendeeRepository;
import org.fossasia.openevent.app.common.data.repository.contract.IEventRepository;
import org.fossasia.openevent.app.common.data.repository.contract.IFAQRepository;
import org.fossasia.openevent.app.common.data.repository.contract.ITicketRepository;

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
    abstract IFAQRepository bindsFAQRepository(FaqRepository faqRepository);
}
