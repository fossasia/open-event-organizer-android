package org.fossasia.openevent.app.common.di.module;

import org.fossasia.openevent.app.data.db.DatabaseRepository;
import org.fossasia.openevent.app.data.db.contract.IDatabaseRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {

    @Provides
    @Singleton
    IDatabaseRepository providesDatabaseRepository() {
        return new DatabaseRepository();
    }

}
