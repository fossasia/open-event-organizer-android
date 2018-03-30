package org.fossasia.openevent.app.common.di.module;

import org.fossasia.openevent.app.data.AuthModel;
import org.fossasia.openevent.app.data.Bus;
import org.fossasia.openevent.app.data.IAuthModel;
import org.fossasia.openevent.app.data.IBus;
import org.fossasia.openevent.app.data.ISharedPreferenceModel;
import org.fossasia.openevent.app.data.IUtilModel;
import org.fossasia.openevent.app.data.SharedPreferenceModel;
import org.fossasia.openevent.app.data.UtilModel;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ModelModule {

    @Binds
    @Singleton
    abstract IBus bindsBus(Bus bus);

    @Binds
    @Singleton
    abstract IUtilModel bindsUtilModel(UtilModel utilModel);

    @Binds
    @Singleton
    abstract ISharedPreferenceModel bindsSharedPreferenceModel(SharedPreferenceModel sharedPreferenceModel);

    @Binds
    @Singleton
    abstract IAuthModel bindsLoginModule(AuthModel authModel);

}
