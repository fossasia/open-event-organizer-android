package org.fossasia.openevent.app.common.di.module;

import android.content.Context;

import org.fossasia.openevent.app.OrgaApplication;

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

}
