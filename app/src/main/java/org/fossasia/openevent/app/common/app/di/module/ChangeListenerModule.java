package org.fossasia.openevent.app.common.app.di.module;

import org.fossasia.openevent.app.common.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.common.data.db.contract.IDatabaseChangeListener;
import org.fossasia.openevent.app.common.data.models.Attendee;

import dagger.Module;
import dagger.Provides;

@Module
public class ChangeListenerModule {

    @Provides
    public IDatabaseChangeListener<Attendee> providesAttendeeChangeListener() {
        return new DatabaseChangeListener<>(Attendee.class);
    }

}
