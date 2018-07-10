package org.fossasia.openevent.app.common.di.module.android;

import org.fossasia.openevent.app.core.attendee.checkin.AttendeeCheckInFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class BarcodeFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract AttendeeCheckInFragment contributeAttendeeCheckinFragment();

}
