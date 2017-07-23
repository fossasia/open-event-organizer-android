package org.fossasia.openevent.app.common.di.module;

import android.content.Context;
import android.content.SharedPreferences;

import com.f2prateek.rx.preferences2.RxSharedPreferences;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.utils.Constants;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AndroidModule {
    // Storing Application instead of Context to prevent memory leaks
    private OrgaApplication application;

    public AndroidModule(Context context) {
        this.application = (OrgaApplication) context.getApplicationContext();
    }

    @Provides
    @Singleton
    Context providesContext() {
        return application;
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
