package org.fossasia.openevent.app.common.app.di.module;

import org.fossasia.openevent.app.module.attendee.checkin.AttendeeCheckInPresenter;
import org.fossasia.openevent.app.module.attendee.checkin.contract.IAttendeeCheckInPresenter;
import org.fossasia.openevent.app.module.attendee.list.AttendeesPresenter;
import org.fossasia.openevent.app.module.attendee.list.contract.IAttendeesPresenter;
import org.fossasia.openevent.app.module.attendee.qrscan.ScanQRPresenter;
import org.fossasia.openevent.app.module.attendee.qrscan.contract.IScanQRPresenter;
import org.fossasia.openevent.app.module.auth.forgot.password.token.request.ForgotPasswordPresenter;
import org.fossasia.openevent.app.module.auth.forgot.password.token.request.contract.IForgotPasswordPresenter;
import org.fossasia.openevent.app.module.auth.forgot.password.token.submit.ResetPasswordByTokenPresenter;
import org.fossasia.openevent.app.module.auth.forgot.password.token.submit.contract.IResetPasswordByTokenPresenter;
import org.fossasia.openevent.app.module.auth.login.LoginPresenter;
import org.fossasia.openevent.app.module.auth.login.contract.ILoginPresenter;
import org.fossasia.openevent.app.module.auth.signup.SignUpPresenter;
import org.fossasia.openevent.app.module.auth.signup.contract.ISignUpPresenter;
import org.fossasia.openevent.app.module.event.about.AboutEventPresenter;
import org.fossasia.openevent.app.module.event.about.contract.IAboutEventPresenter;
import org.fossasia.openevent.app.module.event.chart.ChartPresenter;
import org.fossasia.openevent.app.module.event.chart.contract.IChartPresenter;
import org.fossasia.openevent.app.module.event.copyright.CreateCopyrightPresenter;
import org.fossasia.openevent.app.module.event.copyright.contract.ICreateCopyrightPresenter;
import org.fossasia.openevent.app.module.event.create.CreateEventPresenter;
import org.fossasia.openevent.app.module.event.create.contract.ICreateEventPresenter;
import org.fossasia.openevent.app.module.event.dashboard.EventDashboardPresenter;
import org.fossasia.openevent.app.module.event.dashboard.contract.IEventDashboardPresenter;
import org.fossasia.openevent.app.module.faq.list.FaqListPresenter;
import org.fossasia.openevent.app.module.faq.list.contract.IFaqListPresenter;
import org.fossasia.openevent.app.module.event.list.EventsPresenter;
import org.fossasia.openevent.app.module.event.list.contract.IEventsPresenter;
import org.fossasia.openevent.app.module.main.MainPresenter;
import org.fossasia.openevent.app.module.main.contract.IMainPresenter;
import org.fossasia.openevent.app.module.organizer.detail.OrganizerDetailPresenter;
import org.fossasia.openevent.app.module.organizer.detail.contract.IOrganizerDetailPresenter;
import org.fossasia.openevent.app.module.organizer.password.ChangePasswordPresenter;
import org.fossasia.openevent.app.module.organizer.password.contract.IChangePasswordPresenter;
import org.fossasia.openevent.app.module.ticket.create.CreateTicketPresenter;
import org.fossasia.openevent.app.module.ticket.create.contract.ICreateTicketPresenter;
import org.fossasia.openevent.app.module.ticket.detail.TicketDetailPresenter;
import org.fossasia.openevent.app.module.ticket.detail.contract.ITicketDetailPresenter;
import org.fossasia.openevent.app.module.ticket.list.TicketsPresenter;
import org.fossasia.openevent.app.module.ticket.list.contract.ITicketsPresenter;

import dagger.Binds;
import dagger.Module;

@SuppressWarnings({"PMD.TooManyMethods", "PMD.CouplingBetweenObjects"}) // Will break cohesion if refactored
@Module
public abstract class PresenterModule {

    @Binds
    abstract ISignUpPresenter bindsSignUpPresenter(SignUpPresenter signUpPresenter);

    @Binds
    abstract ILoginPresenter bindsLoginPresenter(LoginPresenter loginPresenter);

    @Binds
    abstract IMainPresenter bindsMainPresenter(MainPresenter mainPresenter);

    @Binds
    abstract IEventsPresenter bindsEventsPresenter(EventsPresenter eventsPresenter);

    @Binds
    abstract IEventDashboardPresenter bindsEventDetailPresenter(EventDashboardPresenter eventDashboardPresenter);

    @Binds
    abstract IChartPresenter bindsChartPresenter(ChartPresenter chartPresenter);

    @Binds
    abstract IAttendeesPresenter bindsAttendeePresenter(AttendeesPresenter attendeesPresenter);

    @Binds
    abstract IScanQRPresenter bindsScanQRPresenter(ScanQRPresenter scanQRPresenter);

    @Binds
    abstract IAttendeeCheckInPresenter bindsAttendeeCheckInPresenter(AttendeeCheckInPresenter attendeeCheckInPresenter);

    @Binds
    abstract ITicketsPresenter bindsTicketsPresenter(TicketsPresenter ticketsPresenter);

    @Binds
    abstract ICreateTicketPresenter bindsCreateTicketPresenter(CreateTicketPresenter createTicketPresenter);

    @Binds
    abstract ITicketDetailPresenter bindsTicketDetailPresenter(TicketDetailPresenter ticketDetailPresenter);

    @Binds
    abstract IAboutEventPresenter bindsAboutEventPresenter(AboutEventPresenter aboutEventPresenter);

    @Binds
    abstract IOrganizerDetailPresenter bindsOrganizerDetailPresenter(OrganizerDetailPresenter organizerDetailPresenter);

    @Binds
    abstract IForgotPasswordPresenter bindsForgotPasswordPresenter(ForgotPasswordPresenter forgotPasswordPresenter);

    @Binds
    abstract IResetPasswordByTokenPresenter bindsResetPasswordByTokenPresenter(ResetPasswordByTokenPresenter resetPasswordByTokenPresenter);

    @Binds
    abstract IChangePasswordPresenter bindsChangePasswordPresenter(ChangePasswordPresenter changePasswordPresenter);

    @Binds
    abstract ICreateEventPresenter bindsCreateEventPresenter(CreateEventPresenter createEventPresenter);

    @Binds
    abstract IFaqListPresenter bindsFaqListPresenter(FaqListPresenter faqListPresenter);

    @Binds
    abstract ICreateCopyrightPresenter bindsCreateCopyrightPresenter(CreateCopyrightPresenter createCopyrightPresenter);
}
