package org.fossasia.openevent.app.common.di.module.android;

import org.fossasia.openevent.app.core.attendee.checkin.AttendeeCheckInFragment;
import org.fossasia.openevent.app.core.attendee.list.AttendeesFragment;
import org.fossasia.openevent.app.core.event.dashboard.EventDashboardFragment;
import org.fossasia.openevent.app.core.event.list.EventListFragment;
import org.fossasia.openevent.app.core.faq.create.CreateFaqFragment;
import org.fossasia.openevent.app.core.faq.list.FaqListFragment;
import org.fossasia.openevent.app.core.feedback.list.FeedbackListFragment;
import org.fossasia.openevent.app.core.settings.SettingsFragment;
import org.fossasia.openevent.app.core.speaker.list.SpeakersFragment;
import org.fossasia.openevent.app.core.speakerscall.detail.SpeakersCallFragment;
import org.fossasia.openevent.app.core.sponsor.list.SponsorsFragment;
import org.fossasia.openevent.app.core.sponsor.create.CreateSponsorFragment;
import org.fossasia.openevent.app.core.sponsor.update.UpdateSponsorFragment;
import org.fossasia.openevent.app.core.ticket.create.CreateTicketFragment;
import org.fossasia.openevent.app.core.ticket.detail.TicketDetailFragment;
import org.fossasia.openevent.app.core.ticket.list.TicketsFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainFragmentBuildersModule {

    // Event

    @ContributesAndroidInjector
    abstract EventDashboardFragment contributeEventDashboardFragment();

    @ContributesAndroidInjector
    abstract EventListFragment contributeEventListFragment();

    // Attendee

    @ContributesAndroidInjector
    abstract AttendeeCheckInFragment contributeAttendeeCheckinFragment();

    @ContributesAndroidInjector
    abstract AttendeesFragment contributeAttendeeFragment();

    // Ticket
    @ContributesAndroidInjector
    abstract TicketsFragment contributeTicketFragment();

    @ContributesAndroidInjector
    abstract TicketDetailFragment contributeTicketDetailFragment();

    @ContributesAndroidInjector
    abstract CreateTicketFragment contributeCreateTicketFragment();

    // Setting

    @ContributesAndroidInjector
    abstract SettingsFragment constributeSettingFragment();

    // FAQ

    @ContributesAndroidInjector
    abstract FaqListFragment contributeFaqListFragment();

    @ContributesAndroidInjector
    abstract CreateFaqFragment contributeCreateFaqFragment();

    //Feedback

    @ContributesAndroidInjector
    abstract FeedbackListFragment contributeFeedbackListFragment();

    // Sponsor

    @ContributesAndroidInjector
    abstract SponsorsFragment contributeSponsorsFragment();

    @ContributesAndroidInjector
    abstract CreateSponsorFragment contributeCreateSponsorFragment();

    @ContributesAndroidInjector
    abstract UpdateSponsorFragment contributeUpdateSponsorFragment();

    // Speaker

    @ContributesAndroidInjector
    abstract SpeakersFragment contributeSpeakersFragment();

    //SpeakersCall

    @ContributesAndroidInjector
    abstract SpeakersCallFragment contributeSpeakersCallFragment();
}

