package com.eventyay.organizer.common.di.module.android;

import com.eventyay.organizer.core.auth.AuthActivity;
import com.eventyay.organizer.core.event.about.AboutEventActivity;
import com.eventyay.organizer.core.event.chart.ChartActivity;
import com.eventyay.organizer.core.event.create.CreateEventActivity;
import com.eventyay.organizer.core.event.create.EventDetailsStepOne;
import com.eventyay.organizer.core.event.create.EventDetailsStepThree;
import com.eventyay.organizer.core.event.create.EventDetailsStepTwo;
import com.eventyay.organizer.core.event.create.UpdateEventFragment;
import com.eventyay.organizer.core.main.MainActivity;
import com.eventyay.organizer.core.orders.create.CreateOrderFragment;
import com.eventyay.organizer.core.orders.onsite.CreateAttendeesFragment;
import com.eventyay.organizer.core.organizer.detail.OrganizerDetailActivity;
import com.eventyay.organizer.core.speaker.details.SpeakerDetailsActivity;
import com.eventyay.organizer.core.speaker.details.SpeakerDetailsFragment;
import com.eventyay.organizer.core.speakerscall.create.CreateSpeakersCallFragment;

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
    abstract UpdateEventFragment contributeCreateEventFragment();

    @ContributesAndroidInjector
    abstract ChartActivity contributeChartActivity();

    @ContributesAndroidInjector
    abstract SpeakerDetailsActivity contributeSpeakerDetailsActivity();

    @ContributesAndroidInjector
    abstract SpeakerDetailsFragment contributeSpeakerDetailsFragment();

    @ContributesAndroidInjector
    abstract CreateSpeakersCallFragment contributeCreateSpeakersCallFragment();

    @ContributesAndroidInjector
    abstract EventDetailsStepOne contributesEventDetailsLevel1();

    @ContributesAndroidInjector
    abstract EventDetailsStepTwo contributesEventDetailsLevel2();

    @ContributesAndroidInjector
    abstract EventDetailsStepThree contributesEventDetailsLevel3();

    @ContributesAndroidInjector
    abstract CreateOrderFragment contributeCreateOrderFragment();

    @ContributesAndroidInjector
    abstract CreateAttendeesFragment contributeCreateAttendeesFragment();
}
