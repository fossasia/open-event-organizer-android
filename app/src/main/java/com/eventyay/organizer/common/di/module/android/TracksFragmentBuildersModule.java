package com.eventyay.organizer.common.di.module.android;

import com.eventyay.organizer.core.session.create.CreateSessionFragment;
import com.eventyay.organizer.core.session.list.SessionsFragment;
import com.eventyay.organizer.core.track.create.CreateTrackFragment;
import com.eventyay.organizer.core.track.list.TracksFragment;
import com.eventyay.organizer.core.track.update.UpdateTrackFragment;

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
