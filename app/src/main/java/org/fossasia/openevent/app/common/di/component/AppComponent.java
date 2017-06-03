package org.fossasia.openevent.app.common.di.component;

import org.fossasia.openevent.app.common.di.module.AndroidModule;
import org.fossasia.openevent.app.common.di.module.DataModule;
import org.fossasia.openevent.app.common.di.module.NetworkModule;
import org.fossasia.openevent.app.common.di.module.PresenterModule;
import org.fossasia.openevent.app.event.attendees.AttendeesFragment;
import org.fossasia.openevent.app.event.detail.EventDetailFragment;
import org.fossasia.openevent.app.events.EventListActivity;
import org.fossasia.openevent.app.login.LoginActivity;
import org.fossasia.openevent.app.qrscan.ScanQRActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
    AndroidModule.class,
    NetworkModule.class,
    DataModule.class,
    PresenterModule.class
})
public interface AppComponent {

    void inject(LoginActivity loginActivity);

    void inject(EventListActivity eventListActivity);

    void inject(EventDetailFragment eventDetailFragment);

    void inject(AttendeesFragment attendeesFragment);

    void inject(ScanQRActivity scanQRActivity);

}
