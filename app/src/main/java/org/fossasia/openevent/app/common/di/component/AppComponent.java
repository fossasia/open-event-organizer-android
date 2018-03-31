package org.fossasia.openevent.app.common.di.component;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.common.di.module.AppModule;
import org.fossasia.openevent.app.common.di.module.android.ActivityBuildersModule;
import org.fossasia.openevent.app.core.attendee.checkin.AttendeeCheckInFragment;
import org.fossasia.openevent.app.core.attendee.checkin.job.AttendeeCheckInJob;
import org.fossasia.openevent.app.core.attendee.list.AttendeesFragment;
import org.fossasia.openevent.app.core.auth.AuthActivity;
import org.fossasia.openevent.app.core.auth.forgot.request.ForgotPasswordFragment;
import org.fossasia.openevent.app.core.auth.forgot.submit.ResetPasswordByTokenFragment;
import org.fossasia.openevent.app.core.auth.login.LoginFragment;
import org.fossasia.openevent.app.core.auth.signup.SignUpFragment;
import org.fossasia.openevent.app.core.event.about.AboutEventFragment;
import org.fossasia.openevent.app.core.event.chart.ChartActivity;
import org.fossasia.openevent.app.core.event.copyright.CreateCopyrightFragment;
import org.fossasia.openevent.app.core.event.copyright.update.UpdateCopyrightFragment;
import org.fossasia.openevent.app.core.event.create.CreateEventFragment;
import org.fossasia.openevent.app.core.event.dashboard.EventDashboardFragment;
import org.fossasia.openevent.app.core.event.list.EventListFragment;
import org.fossasia.openevent.app.core.faq.create.CreateFaqFragment;
import org.fossasia.openevent.app.core.faq.list.FaqListFragment;
import org.fossasia.openevent.app.core.main.MainActivity;
import org.fossasia.openevent.app.core.organizer.detail.OrganizerDetailFragment;
import org.fossasia.openevent.app.core.organizer.password.ChangePasswordFragment;
import org.fossasia.openevent.app.core.settings.SettingsFragment;
import org.fossasia.openevent.app.core.ticket.create.CreateTicketFragment;
import org.fossasia.openevent.app.core.ticket.detail.TicketDetailFragment;
import org.fossasia.openevent.app.core.ticket.list.TicketsFragment;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

@Singleton
@Component(modules = {
    AndroidInjectionModule.class,
    ActivityBuildersModule.class,
    AppModule.class
})
@SuppressWarnings("PMD.TooManyMethods") // Will contain entry for all injectable classes
public interface AppComponent extends AndroidInjector<OrgaApplication> {

    void inject(OrgaApplication orgaApplication);

    void inject(MainActivity mainActivity);

    void inject(EventListFragment eventListFragment);

    void inject(EventDashboardFragment eventDashboardFragment);

    void inject(AttendeesFragment attendeesFragment);

    void inject(AttendeeCheckInFragment attendeeCheckInFragment);

    void inject(SettingsFragment settingsFragment);

    void inject(ChartActivity chartActivity);

    void inject(TicketsFragment ticketsFragment);

    void inject(CreateTicketFragment createTicketFragment);

    void inject(LoginFragment loginFragment);

    void inject(AuthActivity authActivity);

    void inject(SignUpFragment signUpFragment);

    void inject(TicketDetailFragment ticketDetailFragment);

    void inject(AttendeeCheckInJob attendeeCheckInJob);

    void inject(AboutEventFragment aboutEventFragment);

    void inject(OrganizerDetailFragment organizerDetailFragment);

    void inject(ForgotPasswordFragment forgotPasswordFragment);

    void inject(ResetPasswordByTokenFragment resetPasswordByTokenFragment);

    void inject(ChangePasswordFragment changePasswordFragment);

    void inject(CreateEventFragment createEventFragment);

    void inject(FaqListFragment faqListFragment);

    void inject(CreateCopyrightFragment createCopyrightFragment);

    void inject(CreateFaqFragment createFaqFragment);

    void inject(UpdateCopyrightFragment updateCopyrightFragment);
}
