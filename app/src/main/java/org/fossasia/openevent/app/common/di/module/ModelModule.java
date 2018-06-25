package org.fossasia.openevent.app.common.di.module;

import org.fossasia.openevent.app.data.auth.AuthServiceImpl;
import org.fossasia.openevent.app.data.AndroidUtils;
import org.fossasia.openevent.app.data.ContextUtils;
import org.fossasia.openevent.app.data.Preferences;
import org.fossasia.openevent.app.data.RxBus;
import org.fossasia.openevent.app.data.auth.AuthService;
import org.fossasia.openevent.app.data.Bus;
import org.fossasia.openevent.app.data.SharedPreferencesImpl;
import org.fossasia.openevent.app.data.encryption.EncryptionService;
import org.fossasia.openevent.app.data.encryption.EncryptionServiceImpl;
import org.fossasia.openevent.app.data.network.ConnectionStatus;
import org.fossasia.openevent.app.data.network.ConnectionStatusImpl;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ModelModule {

    @Binds
    @Singleton
    abstract Bus bindsBus(RxBus bus);

    @Binds
    @Singleton
    abstract ContextUtils bindsUtilModel(AndroidUtils utilModel);

    @Binds
    @Singleton
    abstract ConnectionStatus bindsConnectionObserver(ConnectionStatusImpl connectionStatus);

    @Binds
    @Singleton
    abstract Preferences bindsSharedPreferenceModel(SharedPreferencesImpl sharedPreferenceModel);

    @Binds
    @Singleton
    abstract AuthService bindsLoginModule(AuthServiceImpl authModel);

    @Binds
    @Singleton
    abstract EncryptionService bindsEncryption(EncryptionServiceImpl encryptionModel);
}
