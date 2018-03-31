package org.fossasia.openevent.app.common.di.component;

import org.fossasia.openevent.app.common.di.module.AppModule;
import org.fossasia.openevent.app.common.di.module.BarcodeModule;
import org.fossasia.openevent.app.core.attendee.qrscan.ScanQRActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
    AppModule.class,
    BarcodeModule.class
})
public interface BarcodeComponent {

    void inject(ScanQRActivity scanQRActivity);

}
