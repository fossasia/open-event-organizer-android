package org.fossasia.openevent.app.event.attendees;

import org.fossasia.openevent.app.data.contract.IEventRepository;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.event.attendees.contract.IAttendeesPresenter;
import org.fossasia.openevent.app.event.attendees.contract.IAttendeesView;
import org.fossasia.openevent.app.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AttendeesPresenter implements IAttendeesPresenter {

    private long eventId;
    private IAttendeesView attendeesView;
    private IEventRepository eventRepository;

    private List<Attendee> attendeeList = new ArrayList<>();

    @Inject
    public AttendeesPresenter(IEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void setAttendeeList(List<Attendee> attendeeList) {
        this.attendeeList.clear();
        this.attendeeList.addAll(attendeeList);
    }

    @Override
    public void attach(long eventId, IAttendeesView attendeesView) {
        this.eventId = eventId;
        this.attendeesView = attendeesView;
    }

    @Override
    public void start() {
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
    public Single<Attendee> getAttendeeById(long attendeeId) {
        return Observable.fromIterable(attendeeList)
            .filter(attendee -> attendee.getId() == attendeeId)
            .firstOrError()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void loadAttendees(boolean forceReload) {
        if(attendeesView == null)
            return;

        attendeesView.showProgressBar(true);
        attendeesView.showScanButton(false);

        eventRepository.getAttendees(eventId, forceReload)
            .toSortedList()
            .subscribeOn(Schedulers.computation())
            .subscribe(attendees -> {
                attendeeList.clear();
                attendeeList.addAll(attendees);

                if (attendeesView == null) return;
                attendeesView.showAttendees(attendees);
                attendeesView.showProgressBar(false);
                attendeesView.showScanButton(true);
            }, throwable -> {
                if (attendeesView == null) return;
                attendeesView.showErrorMessage(throwable.getMessage());
                attendeesView.showProgressBar(false);
            });
    }

    @Override
    public void toggleAttendeeCheckStatus(Attendee attendee) {
        if(attendeesView == null)
            return;

        attendeesView.showProgressBar(true);

        eventRepository.toggleAttendeeCheckStatus(eventId, attendee.getId())
            .subscribe(this::processUpdatedAttendee, throwable -> {
                if(attendeesView == null)
                    return;

                attendeesView.showErrorMessage(throwable.getMessage());
                attendeesView.showProgressBar(false);
            });
    }

    private void processUpdatedAttendee(Attendee attendee) {
        System.out.println(attendeeList);
        Utils.indexOf(attendeeList, attendee,
            (first, second) -> first.getId() == second.getId())
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(position -> {
                if(attendeesView == null)
                    return;

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
