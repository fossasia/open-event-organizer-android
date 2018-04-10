package org.fossasia.openevent.app.common.di.module.android;

import org.fossasia.openevent.app.core.organizer.detail.OrganizerDetailFragment;
import org.fossasia.openevent.app.core.organizer.password.ChangePasswordFragment;
import org.fossasia.openevent.app.core.organizer.update.UpdateOrganizerInfoFragment;

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
