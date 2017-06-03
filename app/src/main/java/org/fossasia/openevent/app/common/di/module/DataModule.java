package org.fossasia.openevent.app.common.di.module;

import org.fossasia.openevent.app.data.EventRepository;
import org.fossasia.openevent.app.data.cache.ICacheModel;
import org.fossasia.openevent.app.data.cache.ObjectCache;
import org.fossasia.openevent.app.data.contract.IEventRepository;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.network.EventService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

    @Provides
    @Singleton
    ICacheModel providesCacheModel() {
        return ObjectCache.getInstance();
    }

    @Provides
    @Singleton
    IEventRepository providesEventRepository(IUtilModel utilModel, ICacheModel cacheModel, EventService eventService) {
        return new EventRepository(utilModel, cacheModel, eventService);
    }
}
