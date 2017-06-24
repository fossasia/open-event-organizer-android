package org.fossasia.openevent.app.common.di.module;

import org.fossasia.openevent.app.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.data.db.contract.IDatabaseChangeListener;
import org.fossasia.openevent.app.data.models.Attendee;

import dagger.Module;
import dagger.Provides;

@Module
public class ChangeListenerModule {

    @Provides
    public IDatabaseChangeListener<Attendee> providesAttendeeChangeListener() {
        return new DatabaseChangeListener<>(Attendee.class);
    }

}
