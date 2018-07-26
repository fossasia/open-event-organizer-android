package com.eventyay.organizer.common.di.module.android;

import com.eventyay.organizer.core.event.about.AboutEventFragment;
import com.eventyay.organizer.core.event.copyright.CreateCopyrightFragment;
import com.eventyay.organizer.core.event.copyright.update.UpdateCopyrightFragment;

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
