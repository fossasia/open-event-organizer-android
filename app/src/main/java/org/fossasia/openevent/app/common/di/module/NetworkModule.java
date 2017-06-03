package org.fossasia.openevent.app.common.di.module;

import org.fossasia.openevent.app.data.LoginModel;
import org.fossasia.openevent.app.data.contract.ILoginModel;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.network.EventService;
import org.fossasia.openevent.app.data.network.NetworkService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class NetworkModule {

    @Provides
    @Singleton
    EventService providesEventService() {
        return NetworkService.getEventService();
    }

    @Provides
    @Singleton
    ILoginModel providesLoginModel(IUtilModel utilModel, EventService eventService) {
        return new LoginModel(utilModel, eventService);
    }
}
