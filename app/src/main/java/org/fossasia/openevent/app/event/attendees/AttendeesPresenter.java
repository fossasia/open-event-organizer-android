package org.fossasia.openevent.app.event.attendees;

import org.fossasia.openevent.app.data.contract.IEventDataRepository;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.event.attendees.contract.IAttendeesPresenter;
import org.fossasia.openevent.app.event.attendees.contract.IAttendeesView;
import org.fossasia.openevent.app.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AttendeesPresenter implements IAttendeesPresenter {

    private long eventId;
    private IAttendeesView attendeesView;
    private IEventDataRepository eventDataRepository;

    private List<Attendee> attendeeList = new ArrayList<>();

    public AttendeesPresenter(long eventId, IAttendeesView attendeesView, IEventDataRepository eventDataRepository) {
        this.eventId = eventId;
        this.attendeesView = attendeesView;
        this.eventDataRepository = eventDataRepository;
    }

    @Override
    public void attach() {
        loadAttendees(false);
    }

    @Override
    public void detach() {
        attendeesView = null;
    }

    @Override
    public List<Attendee> getAttendees() {
        return attendeeList;
    }

    @Override
    public void loadAttendees(boolean forceReload) {
        if(attendeesView == null)
            return;

        attendeesView.showProgressBar(true);
        attendeesView.showScanButton(false);

        eventDataRepository.getAttendees(eventId, forceReload)
            .subscribe(attendees -> {
                attendeeList.clear();
                attendeeList.addAll(attendees);

                attendeesView.showAttendees(attendees);
                attendeesView.showProgressBar(false);
                attendeesView.showScanButton(true);
            }, throwable -> {
                attendeesView.showErrorMessage(throwable.getMessage());
                attendeesView.showProgressBar(false);
            });
    }

    @Override
    public void toggleAttendeeCheckStatus(Attendee attendee) {
        attendeesView.showProgressBar(true);

        eventDataRepository.toggleAttendeeCheckStatus(eventId, attendee.getId())
            .subscribe(this::processUpdatedAttendee, throwable -> {
                attendeesView.showErrorMessage(throwable.getMessage());
                attendeesView.showProgressBar(false);
            });
    }

    private void processUpdatedAttendee(Attendee attendee) {
        Utils.indexOf(attendeeList, attendee,
            (first, second) -> first.getId() == second.getId())
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(position -> {
                if (position == -1)
                    attendeesView.showErrorMessage("Error in updating Attendee");
                else {
                    attendeeList.set(position, attendee);
                    attendeesView.updateAttendee(position, attendee);
                }
                attendeesView.showProgressBar(false);
            });
    }

    public IAttendeesView getView() {
        return attendeesView;
    }

}
