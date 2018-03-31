package org.fossasia.openevent.app.common.di.module.android;

import org.fossasia.openevent.app.core.attendee.qrscan.ScanQRActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class BarcodeActivityBuildersModule {

    @ContributesAndroidInjector
    abstract ScanQRActivity contributeScanQRActivity();

}
