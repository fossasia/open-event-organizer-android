package org.fossasia.openevent.app.common.app.di.component;

import org.fossasia.openevent.app.common.app.di.module.DataModule;
import org.fossasia.openevent.app.common.app.di.module.BarcodeModule;
import org.fossasia.openevent.app.common.app.di.module.NetworkModule;
import org.fossasia.openevent.app.common.app.di.module.PresenterModule;
import org.fossasia.openevent.app.module.attendee.qrscan.ScanQRActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
    DataModule.class,
    NetworkModule.class,
    PresenterModule.class,
    BarcodeModule.class
})
public interface BarcodeComponent {

    void inject(ScanQRActivity scanQRActivity);

}
