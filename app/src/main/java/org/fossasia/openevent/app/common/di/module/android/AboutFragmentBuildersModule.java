package org.fossasia.openevent.app.common.di.module.android;

import org.fossasia.openevent.app.core.event.about.AboutEventFragment;
import org.fossasia.openevent.app.core.event.copyright.CreateCopyrightFragment;
import org.fossasia.openevent.app.core.event.copyright.update.UpdateCopyrightFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class AboutFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract AboutEventFragment contributeAboutEventFragment();

    @ContributesAndroidInjector
    abstract CreateCopyrightFragment contributeCreateCopyrightFragment();

    @ContributesAndroidInjector
    abstract UpdateCopyrightFragment contributeUpdateCopyrightFragment();

}
