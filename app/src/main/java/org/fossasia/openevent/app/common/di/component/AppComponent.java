package org.fossasia.openevent.app.common.di.component;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.common.di.module.DataModule;
import org.fossasia.openevent.app.common.di.module.NetworkModule;
import org.fossasia.openevent.app.common.di.module.PresenterModule;
import org.fossasia.openevent.app.event.attendees.AttendeesFragment;
import org.fossasia.openevent.app.event.checkin.AttendeeCheckInFragment;
import org.fossasia.openevent.app.event.detail.EventDetailFragment;
import org.fossasia.openevent.app.events.EventListFragment;
import org.fossasia.openevent.app.login.LoginActivity;
import org.fossasia.openevent.app.main.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
    DataModule.class,
    NetworkModule.class,
    PresenterModule.class
})
public interface AppComponent {

    void inject(LoginActivity loginActivity);

    void inject(MainActivity mainActivity);

    void inject(EventListFragment eventListFragment);

    void inject(EventDetailFragment eventDetailFragment);

    void inject(AttendeesFragment attendeesFragment);

    void inject(AttendeeCheckInFragment attendeeCheckInFragment);

    void inject(OrgaApplication orgaApplication);

}
