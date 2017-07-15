package org.fossasia.openevent.app.event.attendees;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.data.db.contract.IDatabaseChangeListener;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.repository.contract.IAttendeeRepository;
import org.fossasia.openevent.app.event.attendees.contract.IAttendeesPresenter;
import org.fossasia.openevent.app.event.attendees.contract.IAttendeesView;

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
    private IAttendeeRepository attendeeRepository;
    private IDatabaseChangeListener<Attendee> attendeeListener;

    private final List<Attendee> attendeeList = new ArrayList<>();

    @Inject
    public AttendeesPresenter(IAttendeeRepository attendeeRepository, IDatabaseChangeListener<Attendee> attendeeListener) {
        this.attendeeRepository = attendeeRepository;
        this.attendeeListener = attendeeListener;
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
        listenToModelChanges();
    }

    @Override
    public void detach() {
        attendeesView = null;
        attendeeListener.stopListening();
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

    private void hideProgress(boolean forceReload) {
        attendeesView.showProgress(false);
        attendeesView.showEmptyView(attendeeList.size() == 0);

        if (forceReload)
            attendeesView.onRefreshComplete();
    }

    @Override
    public void loadAttendees(boolean forceReload) {
        if(attendeesView == null)
            return;

        attendeesView.showProgress(true);
        attendeesView.showEmptyView(false);
        attendeesView.showScanButton(false);

        attendeeRepository.getAttendees(eventId, forceReload)
            .toSortedList()
            .subscribeOn(Schedulers.computation())
            .subscribe(attendees -> {
                attendeeList.clear();
                attendeeList.addAll(attendees);

                if (attendeesView == null) return;
                attendeesView.showResults(attendees);
                hideProgress(forceReload);
                attendeesView.showScanButton(!attendeeList.isEmpty());
            }, throwable -> {
                if (attendeesView == null) return;
                attendeesView.showError(throwable.getMessage());
                hideProgress(forceReload);
                attendeesView.showScanButton(!attendeeList.isEmpty());
            });
    }

    private void listenToModelChanges() {
        attendeeListener.startListening();

        attendeeListener.getNotifier()
            .filter(attendeeModelChange -> attendeeModelChange.getAction().equals(BaseModel.Action.UPDATE))
            .map(DatabaseChangeListener.ModelChange::getModel)
            .subscribe(attendee -> {
                if (attendeesView == null)
                    return;
                attendeesView.updateAttendee(attendee);
            }, throwable -> {
                if (attendeesView == null)
                    return;
                attendeesView.showError(throwable.getMessage());
            });
    }

    public IAttendeesView getView() {
        return attendeesView;
    }

}
