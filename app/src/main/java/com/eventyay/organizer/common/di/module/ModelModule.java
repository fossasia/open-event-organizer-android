package com.eventyay.organizer.common.di.module;

import com.eventyay.organizer.data.AndroidUtils;
import com.eventyay.organizer.data.Bus;
import com.eventyay.organizer.data.ContextUtils;
import com.eventyay.organizer.data.Preferences;
import com.eventyay.organizer.data.RxBus;
import com.eventyay.organizer.data.SharedPreferencesImpl;
import com.eventyay.organizer.data.auth.AuthService;
import com.eventyay.organizer.data.auth.AuthServiceImpl;
import com.eventyay.organizer.data.encryption.EncryptionService;
import com.eventyay.organizer.data.encryption.EncryptionServiceImpl;
import com.eventyay.organizer.data.network.ConnectionStatus;
import com.eventyay.organizer.data.network.ConnectionStatusImpl;

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
