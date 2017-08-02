package org.fossasia.openevent.app.module.attendee.list;

import android.support.annotation.VisibleForTesting;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.common.app.lifecycle.presenter.BaseDetailPresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.common.data.db.contract.IDatabaseChangeListener;
import org.fossasia.openevent.app.common.data.models.Attendee;
import org.fossasia.openevent.app.common.data.repository.contract.IAttendeeRepository;
import org.fossasia.openevent.app.module.attendee.list.contract.IAttendeesPresenter;
import org.fossasia.openevent.app.module.attendee.list.contract.IAttendeesView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.emptiable;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.erroneous;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.progressiveErroneousRefresh;

public class AttendeesPresenter extends BaseDetailPresenter<Long, IAttendeesView> implements IAttendeesPresenter {

    private IAttendeeRepository attendeeRepository;
    private IDatabaseChangeListener<Attendee> attendeeListener;

    private final List<Attendee> attendeeList = new ArrayList<>();

    @Inject
    public AttendeesPresenter(IAttendeeRepository attendeeRepository, IDatabaseChangeListener<Attendee> attendeeListener) {
        this.attendeeRepository = attendeeRepository;
        this.attendeeListener = attendeeListener;
    }

    @Override
    public void start() {
        loadAttendees(false);
        listenToModelChanges();
    }

    @Override
    public void detach() {
        attendeeListener.stopListening();
        super.detach();
    }

    @Override
    public List<Attendee> getAttendees() {
        return attendeeList;
    }

    @Override
    public void loadAttendees(boolean forceReload) {
        if(getView() == null)
            return;

        getView().showScanButton(false);

        getAttendeeSource(forceReload)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneousRefresh(getView(), forceReload))
            .toSortedList()
            .compose(emptiable(getView(), attendeeList))
            .doFinally(() -> getView().showScanButton(!attendeeList.isEmpty()))
            .subscribe(Logger::logSuccess, Logger::logError);
    }

    private Observable<Attendee> getAttendeeSource(boolean forceReload) {
        if (!forceReload && !attendeeList.isEmpty() && isRotated())
            return Observable.fromIterable(attendeeList);
        else
            return attendeeRepository.getAttendees(getId(), forceReload);
    }

    private void listenToModelChanges() {
        attendeeListener.startListening();

        attendeeListener.getNotifier()
            .compose(dispose(getDisposable()))
            .compose(erroneous(getView()))
            .filter(attendeeModelChange -> attendeeModelChange.getAction().equals(BaseModel.Action.UPDATE))
            .map(DatabaseChangeListener.ModelChange::getModel)
            .subscribe(attendee -> getView().updateAttendee(attendee), Logger::logError);
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public IAttendeesView getView() {
        return super.getView();
    }

    @VisibleForTesting
    public void setAttendeeList(List<Attendee> attendeeList) {
        this.attendeeList.clear();
        this.attendeeList.addAll(attendeeList);
    }

}
