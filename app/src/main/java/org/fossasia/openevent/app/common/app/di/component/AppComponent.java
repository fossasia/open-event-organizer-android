package org.fossasia.openevent.app.common.app.di.component;

import org.fossasia.openevent.app.common.app.di.module.DataModule;
import org.fossasia.openevent.app.common.app.di.module.NetworkModule;
import org.fossasia.openevent.app.common.app.di.module.PresenterModule;
import org.fossasia.openevent.app.module.attendee.checkin.AttendeeCheckInFragment;
import org.fossasia.openevent.app.module.attendee.checkin.job.AttendeeCheckInJob;
import org.fossasia.openevent.app.module.attendee.list.AttendeesFragment;
import org.fossasia.openevent.app.module.auth.AuthActivity;
import org.fossasia.openevent.app.module.auth.forgot.password.token.request.ForgotPasswordFragment;
import org.fossasia.openevent.app.module.auth.forgot.password.token.submit.ResetPasswordByTokenFragment;
import org.fossasia.openevent.app.module.auth.login.LoginFragment;
import org.fossasia.openevent.app.module.auth.signup.SignUpFragment;
import org.fossasia.openevent.app.module.event.about.AboutEventFragment;
import org.fossasia.openevent.app.module.event.chart.ChartActivity;
import org.fossasia.openevent.app.module.event.copyright.CreateCopyrightFragment;
import org.fossasia.openevent.app.module.event.copyright.update.UpdateCopyrightFragment;
import org.fossasia.openevent.app.module.event.create.CreateEventFragment;
import org.fossasia.openevent.app.module.event.dashboard.EventDashboardFragment;
import org.fossasia.openevent.app.module.event.list.EventListFragment;
import org.fossasia.openevent.app.module.faq.create.CreateFaqFragment;
import org.fossasia.openevent.app.module.faq.list.FaqListFragment;
import org.fossasia.openevent.app.module.main.MainActivity;
import org.fossasia.openevent.app.module.organizer.detail.OrganizerDetailFragment;
import org.fossasia.openevent.app.module.organizer.password.ChangePasswordFragment;
import org.fossasia.openevent.app.module.settings.SettingsFragment;
import org.fossasia.openevent.app.module.ticket.create.CreateTicketFragment;
import org.fossasia.openevent.app.module.ticket.detail.TicketDetailFragment;
import org.fossasia.openevent.app.module.ticket.list.TicketsFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
    DataModule.class,
    NetworkModule.class,
    PresenterModule.class
})
@SuppressWarnings("PMD.TooManyMethods") // Will contain entry for all injectable classes
public interface AppComponent {

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
