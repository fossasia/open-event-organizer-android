package org.fossasia.openevent.app.common.di.module;

import org.fossasia.openevent.app.data.Bus;
import org.fossasia.openevent.app.data.LoginModel;
import org.fossasia.openevent.app.data.SharedPreferenceModel;
import org.fossasia.openevent.app.data.UtilModel;
import org.fossasia.openevent.app.data.contract.IBus;
import org.fossasia.openevent.app.data.contract.ILoginModel;
import org.fossasia.openevent.app.data.contract.ISharedPreferenceModel;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.repository.AttendeeRepository;
import org.fossasia.openevent.app.data.repository.EventRepository;
import org.fossasia.openevent.app.data.repository.contract.IAttendeeRepository;
import org.fossasia.openevent.app.data.repository.contract.IEventRepository;

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
    abstract ILoginModel bindsLoginModule(LoginModel loginModel);

    @Binds
    @Singleton
    abstract IEventRepository bindsEventRepository(EventRepository eventRepository);

    @Binds
    @Singleton
    abstract IAttendeeRepository providesAttendeeRepository(AttendeeRepository attendeeRepository);
}
