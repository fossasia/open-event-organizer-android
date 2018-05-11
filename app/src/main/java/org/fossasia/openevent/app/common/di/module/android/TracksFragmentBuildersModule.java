package org.fossasia.openevent.app.common.di.module.android;

import org.fossasia.openevent.app.core.session.create.CreateSessionFragment;
import org.fossasia.openevent.app.core.session.list.SessionsFragment;
import org.fossasia.openevent.app.core.track.create.CreateTrackFragment;
import org.fossasia.openevent.app.core.track.list.TracksFragment;
import org.fossasia.openevent.app.core.track.update.UpdateTrackFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class TracksFragmentBuildersModule {

    // Tracks

    @ContributesAndroidInjector
    abstract TracksFragment contributeTracksFragment();

    @ContributesAndroidInjector
    abstract CreateTrackFragment contributeCreateTrackFragment();

    @ContributesAndroidInjector
    abstract UpdateTrackFragment contributeUpdateTrackFragment();

    // Session

    @ContributesAndroidInjector
    abstract SessionsFragment contributeSessionFragment();

    @ContributesAndroidInjector
    abstract CreateSessionFragment createSessionFragment();
}
