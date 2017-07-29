package org.fossasia.openevent.app.common.app.di.module;

import org.fossasia.openevent.app.module.attendee.checkin.AttendeeCheckInPresenter;
import org.fossasia.openevent.app.module.attendee.checkin.contract.IAttendeeCheckInPresenter;
import org.fossasia.openevent.app.module.attendee.list.AttendeesPresenter;
import org.fossasia.openevent.app.module.attendee.list.contract.IAttendeesPresenter;
import org.fossasia.openevent.app.module.attendee.qrscan.ScanQRPresenter;
import org.fossasia.openevent.app.module.attendee.qrscan.contract.IScanQRPresenter;
import org.fossasia.openevent.app.module.event.chart.ChartPresenter;
import org.fossasia.openevent.app.module.event.chart.contract.IChartPresenter;
import org.fossasia.openevent.app.module.event.dashboard.EventDashboardPresenter;
import org.fossasia.openevent.app.module.event.dashboard.contract.IEventDashboardPresenter;
import org.fossasia.openevent.app.module.event.list.EventsPresenter;
import org.fossasia.openevent.app.module.event.list.contract.IEventsPresenter;
import org.fossasia.openevent.app.module.login.LoginPresenter;
import org.fossasia.openevent.app.module.login.contract.ILoginPresenter;
import org.fossasia.openevent.app.module.main.MainPresenter;
import org.fossasia.openevent.app.module.main.contract.IMainPresenter;
import org.fossasia.openevent.app.module.ticket.create.CreateTicketPresenter;
import org.fossasia.openevent.app.module.ticket.create.contract.ICreateTicketPresenter;
import org.fossasia.openevent.app.module.ticket.list.TicketsPresenter;
import org.fossasia.openevent.app.module.ticket.list.contract.ITicketsPresenter;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class PresenterModule {

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

}
