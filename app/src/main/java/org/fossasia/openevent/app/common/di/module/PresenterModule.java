package org.fossasia.openevent.app.common.di.module;

import org.fossasia.openevent.app.data.contract.IEventRepository;
import org.fossasia.openevent.app.data.contract.ILoginModel;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.db.contract.IDatabaseChangeListener;
import org.fossasia.openevent.app.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.event.attendees.AttendeesPresenter;
import org.fossasia.openevent.app.event.checkin.AttendeeCheckInPresenter;
import org.fossasia.openevent.app.event.checkin.contract.IAttendeeCheckInPresenter;
import org.fossasia.openevent.app.event.attendees.contract.IAttendeesPresenter;
import org.fossasia.openevent.app.event.detail.EventDetailPresenter;
import org.fossasia.openevent.app.event.detail.contract.IEventDetailPresenter;
import org.fossasia.openevent.app.events.EventsPresenter;
import org.fossasia.openevent.app.events.contract.IEventsPresenter;
import org.fossasia.openevent.app.login.LoginPresenter;
import org.fossasia.openevent.app.login.contract.ILoginPresenter;
import org.fossasia.openevent.app.qrscan.ScanQRPresenter;
import org.fossasia.openevent.app.qrscan.contract.IScanQRPresenter;

import dagger.Module;
import dagger.Provides;

@Module(includes = { AndroidModule.class, DatabaseModule.class, ChangeListenerModule.class })
public class PresenterModule {

    @Provides
    ILoginPresenter providesLoginPresenter(ILoginModel loginModel, IUtilModel utilModel) {
        return new LoginPresenter(loginModel, utilModel);
    }

    @Provides
    IEventsPresenter providesEventPresenter(IEventRepository eventRepository, ILoginModel loginModel) {
        return new EventsPresenter(eventRepository, loginModel);
    }

    @Provides
    IEventDetailPresenter providesEventDetailPresenter(IEventRepository eventRepository) {
        return new EventDetailPresenter(eventRepository);
    }

    @Provides
    IAttendeesPresenter providesAttendeePresenter(IEventRepository eventRepository, IDatabaseChangeListener<Attendee> attendeeChangeListener) {
        return new AttendeesPresenter(eventRepository, attendeeChangeListener);
    }

    @Provides
    IScanQRPresenter providesScanQRPresenter(IEventRepository eventRepository) {
        return new ScanQRPresenter(eventRepository);
    }

    @Provides
    IAttendeeCheckInPresenter providesAttendeeCheckInPresenter(IDatabaseRepository databaseRepository, IEventRepository eventRepository) {
        return new AttendeeCheckInPresenter(databaseRepository, eventRepository);
    }

}
