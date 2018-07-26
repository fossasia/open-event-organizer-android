package org.fossasia.openevent.app.common.di.component;

import org.fossasia.openevent.app.common.di.module.android.BarcodeFragmentBuildersModule;
import org.fossasia.openevent.app.core.attendee.qrscan.ScanQRActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FlavorModule {

    @ContributesAndroidInjector(modules = BarcodeFragmentBuildersModule.class)
    abstract ScanQRActivity contributeScanQRActivity();
}
