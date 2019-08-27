package com.eventyay.organizer.common.di.module.android;

import com.eventyay.organizer.core.organizer.detail.OrganizerDetailFragment;
import com.eventyay.organizer.core.organizer.password.ChangePasswordFragment;
import com.eventyay.organizer.core.organizer.update.UpdateOrganizerInfoFragment;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class OrganizerFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract OrganizerDetailFragment contributeOrganizerDetailFragment();

    @ContributesAndroidInjector
    abstract ChangePasswordFragment contributeChangePasswordFragment();

    @ContributesAndroidInjector
    abstract UpdateOrganizerInfoFragment contributeUpdateOrganizerInfoFragment();
}
