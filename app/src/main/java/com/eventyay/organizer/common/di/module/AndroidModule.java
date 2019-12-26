package com.eventyay.organizer.common.di.module;

import android.content.Context;
import android.content.SharedPreferences;

import com.eventyay.organizer.OrgaProvider;
import com.eventyay.organizer.common.Constants;
import com.f2prateek.rx.preferences2.RxSharedPreferences;

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
