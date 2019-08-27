package com.eventyay.organizer.common.di.module.android;

import com.eventyay.organizer.core.attendee.qrscan.ScanQRActivity;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FlavorModule {

    @ContributesAndroidInjector(modules = BarcodeFragmentBuildersModule.class)
    abstract ScanQRActivity contributeScanQRActivity();
}
