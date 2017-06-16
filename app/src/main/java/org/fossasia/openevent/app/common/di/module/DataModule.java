package org.fossasia.openevent.app.common.di.module;

import android.content.Context;

import org.fossasia.openevent.app.data.EventRepository;
import org.fossasia.openevent.app.data.LoginModel;
import org.fossasia.openevent.app.data.UtilModel;
import org.fossasia.openevent.app.data.contract.IEventRepository;
import org.fossasia.openevent.app.data.contract.ILoginModel;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.data.network.EventService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = { AndroidModule.class, DatabaseModule.class, NetworkModule.class })
public class DataModule {

    @Provides
    @Singleton
    IUtilModel providesUtilModel(Context context) {
        return new UtilModel(context);
    }

    @Provides
    @Singleton
    ILoginModel providesLoginModel(IUtilModel utilModel, EventService eventService, IDatabaseRepository databaseRepository) {
        return new LoginModel(utilModel, eventService, databaseRepository);
    }

    @Provides
    @Singleton
    IEventRepository providesEventRepository(IUtilModel utilModel, IDatabaseRepository databaseRepository, EventService eventService) {
        return new EventRepository(utilModel, databaseRepository, eventService);
    }

}
