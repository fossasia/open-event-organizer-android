package org.fossasia.openevent.app.common.di.module;

import org.fossasia.openevent.app.data.EventRepository;
import org.fossasia.openevent.app.data.LoginModel;
import org.fossasia.openevent.app.data.UtilModel;
import org.fossasia.openevent.app.data.contract.IEventRepository;
import org.fossasia.openevent.app.data.contract.ILoginModel;
import org.fossasia.openevent.app.data.contract.IUtilModel;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class DataModule {

    @Binds
    @Singleton
    abstract IUtilModel bindsUtilModel(UtilModel utilModel);

    @Binds
    @Singleton
    abstract ILoginModel bindsLoginModule(LoginModel loginModel);

    @Binds
    @Singleton
    abstract IEventRepository bindsEventRepository(EventRepository eventRepository);

}
