package com.eventyay.organizer.common.di.component;

import com.eventyay.organizer.OrgaApplication;
import com.eventyay.organizer.common.di.module.AppModule;
import com.eventyay.organizer.common.di.module.android.ActivityBuildersModule;
import com.eventyay.organizer.data.attendee.AttendeeCheckInWork;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

@Singleton
@Component(modules = {
    AndroidInjectionModule.class,
    ActivityBuildersModule.class,
    AppModule.class
})
public interface AppComponent extends AndroidInjector<OrgaApplication> {

    void inject(OrgaApplication orgaApplication);

    void inject(AttendeeCheckInWork attendeeCheckInWork);
}
