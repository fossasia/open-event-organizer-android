package org.fossasia.openevent.app.common.di.module;

import org.fossasia.openevent.app.data.db.DatabaseRepository;
import org.fossasia.openevent.app.data.db.contract.IDatabaseRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module(includes = ChangeListenerModule.class)
public abstract class DatabaseModule {

    @Binds
    @Singleton
    abstract IDatabaseRepository providesDatabaseRepository(DatabaseRepository databaseRepository);

}
