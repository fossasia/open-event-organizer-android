package org.fossasia.openevent.app.common.app.di.module;

import android.content.Context;
import android.content.SharedPreferences;

import com.f2prateek.rx.preferences2.RxSharedPreferences;

import org.fossasia.openevent.app.OrgaProvider;
import org.fossasia.openevent.app.common.Constants;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AndroidModule {

    @Provides
    @Singleton
    Context providesContext() {
        return OrgaProvider.context;
    }

    @Provides
    @Singleton
    SharedPreferences sharedPreferences(Context context) {
        return context.getSharedPreferences(Constants.FOSS_PREFS, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    RxSharedPreferences rxSharedPreferences(SharedPreferences sharedPreferences) {
        return RxSharedPreferences.create(sharedPreferences);
    }

}
