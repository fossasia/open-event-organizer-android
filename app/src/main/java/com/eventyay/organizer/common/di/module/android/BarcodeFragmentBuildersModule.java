package com.eventyay.organizer.common.di.module.android;

import com.eventyay.organizer.core.attendee.checkin.AttendeeCheckInFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class BarcodeFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract AttendeeCheckInFragment contributeAttendeeCheckinFragment();

}
