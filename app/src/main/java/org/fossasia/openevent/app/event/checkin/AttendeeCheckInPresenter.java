package org.fossasia.openevent.app.event.checkin;

import org.fossasia.openevent.app.data.contract.IEventRepository;
import org.fossasia.openevent.app.data.db.contract.IDatabaseRepository;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Attendee_Table;
import org.fossasia.openevent.app.event.checkin.contract.IAttendeeCheckInPresenter;
import org.fossasia.openevent.app.event.checkin.contract.IAttendeeCheckInView;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AttendeeCheckInPresenter implements IAttendeeCheckInPresenter {

    private long attendeeId;
    private IAttendeeCheckInView attendeeCheckInView;
    private IEventRepository eventRepository;
    private IDatabaseRepository databaseRepository;

    private Attendee attendee;

    @Inject
    public AttendeeCheckInPresenter(IDatabaseRepository databaseRepository, IEventRepository eventRepository) {
        this.databaseRepository = databaseRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public void attach(long attendeeId, IAttendeeCheckInView attendeeCheckInView) {
        this.attendeeId = attendeeId;
        this.attendeeCheckInView = attendeeCheckInView;
    }

    @Override
    public void start() {
        loadAttendee();
    }

    @Override
    public void detach() {
        attendeeCheckInView = null;
    }

    private void loadAttendee() {
        databaseRepository.getItems(Attendee.class, Attendee_Table.id.eq(attendeeId))
            .firstOrError()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(attendee -> {
                this.attendee = attendee;
                if (attendeeCheckInView != null)
                    attendeeCheckInView.showAttendee(attendee);
            }, Throwable::printStackTrace);
    }

    @Override
    public void toggleCheckIn() {
        attendeeCheckInView.showProgress(true);

        eventRepository.toggleAttendeeCheckStatus(attendee.getEventId(), attendeeId)
            .subscribe(completed -> {
                if (attendeeCheckInView == null)
                    return;

                attendee = completed;

                String status = attendee.isCheckedIn() ? "Checked In" : "Checked Out";

                attendeeCheckInView.showAttendee(attendee);
                attendeeCheckInView.onSuccess(status);
                attendeeCheckInView.showProgress(false);
                //attendeeCheckInView.dismiss();
            }, throwable -> {
                throwable.printStackTrace();
                attendeeCheckInView.onError(throwable.getMessage());
                attendeeCheckInView.showProgress(false);
                //attendeeCheckInView.dismiss();
            });
    }
}
