package org.fossasia.openevent.app.common.di.module.android;

import org.fossasia.openevent.app.core.auth.AuthActivity;
import org.fossasia.openevent.app.core.event.about.AboutEventActivity;
import org.fossasia.openevent.app.core.event.chart.ChartActivity;
import org.fossasia.openevent.app.core.event.create.CreateEventActivity;
import org.fossasia.openevent.app.core.event.create.CreateEventFragment;
import org.fossasia.openevent.app.core.main.MainActivity;
import org.fossasia.openevent.app.core.organizer.detail.OrganizerDetailActivity;
import org.fossasia.openevent.app.core.speaker.details.SpeakerDetailsActivity;
import org.fossasia.openevent.app.core.speaker.details.SpeakerDetailsFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {

    @ContributesAndroidInjector(modules = {MainFragmentBuildersModule.class, TracksFragmentBuildersModule.class})
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector(modules = AuthFragmentBuildersModule.class)
    abstract AuthActivity contributeAuthActivity();

    @ContributesAndroidInjector(modules = AboutFragmentBuildersModule.class)
    abstract AboutEventActivity contributeEventActivity();

    @ContributesAndroidInjector(modules = OrganizerFragmentBuildersModule.class)
    abstract OrganizerDetailActivity contributeOrganizerDetailActivity();

    @ContributesAndroidInjector
    abstract CreateEventActivity contributeCreateEventActivity();

    @ContributesAndroidInjector
    abstract CreateEventFragment contributeCreateEventFragment();

    @ContributesAndroidInjector
    abstract ChartActivity contributeChartActivity();

    @ContributesAndroidInjector
    abstract SpeakerDetailsActivity contributeSpeakerDetailsActivity();

    @ContributesAndroidInjector
    abstract SpeakerDetailsFragment contributeSpeakerDetailsFragment();

}
