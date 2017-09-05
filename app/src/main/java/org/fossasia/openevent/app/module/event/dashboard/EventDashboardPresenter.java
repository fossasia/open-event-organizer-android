package org.fossasia.openevent.app.module.event.dashboard;

import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.app.lifecycle.presenter.BaseDetailPresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.contract.IUtilModel;
import org.fossasia.openevent.app.common.data.models.Attendee;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.repository.contract.IAttendeeRepository;
import org.fossasia.openevent.app.common.data.repository.contract.IEventRepository;
import org.fossasia.openevent.app.module.event.dashboard.analyser.ChartAnalyser;
import org.fossasia.openevent.app.module.event.dashboard.analyser.TicketAnalyser;
import org.fossasia.openevent.app.module.event.dashboard.contract.IEventDashboardPresenter;
import org.fossasia.openevent.app.module.event.dashboard.contract.IEventDashboardView;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.progressiveErroneous;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.progressiveErroneousRefresh;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.result;

public class EventDashboardPresenter extends BaseDetailPresenter<Long, IEventDashboardView> implements IEventDashboardPresenter {

    private Event event;
    private List<Attendee> attendees;
    private final IEventRepository eventRepository;
    private final IAttendeeRepository attendeeRepository;
    private final TicketAnalyser ticketAnalyser;
    private final ChartAnalyser chartAnalyser;
    private final IUtilModel utilModel;

    @Inject
    public EventDashboardPresenter(IEventRepository eventRepository, IAttendeeRepository attendeeRepository,
                                   TicketAnalyser ticketAnalyser, ChartAnalyser chartAnalyser, IUtilModel utilModel) {
        this.eventRepository = eventRepository;
        this.ticketAnalyser = ticketAnalyser;
        this.attendeeRepository = attendeeRepository;
        this.chartAnalyser = chartAnalyser;
        this.utilModel = utilModel;
    }

    @Override
    public void start() {
        loadDetails(false);
    }

    @Override
    public void loadDetails(boolean forceReload) {
        if (getView() == null)
            return;

        loadChart();
        getEventSource(forceReload)
            .compose(dispose(getDisposable()))
            .compose(result(getView()))
            .flatMap(loadedEvent -> {
                this.event = loadedEvent;
                ticketAnalyser.analyseTotalTickets(event);
                return getAttendeeSource(forceReload);
            })
            .compose(progressiveErroneousRefresh(getView(), forceReload))
            .toList()
            .subscribe(attendees -> {
                this.attendees = attendees;
                ticketAnalyser.analyseSoldTickets(event, attendees);

                if (forceReload) {
                    chartAnalyser.reset();
                    loadChart();
                }
            }, Logger::logError);
    }

    @Override
    public void toggleState() {
        event.state = Event.STATE_DRAFT.equals(event.state) ? Event.STATE_PUBLISHED : Event.STATE_DRAFT;
        eventRepository.updateEvent(event)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .doFinally(() -> getView().showResult(event))
            .subscribe(updatedEvent -> {
                event.state = updatedEvent.state;
                final int successMessage = Event.STATE_PUBLISHED.equals(event.state) ? R.string.publish_success : R.string.draft_success;
                getView().onSuccess(utilModel.getResourceString(successMessage));
            },
            throwable -> event.state = Event.STATE_DRAFT.equals(event.state) ? Event.STATE_PUBLISHED : Event.STATE_DRAFT);

    }

    private void loadChart() {
        chartAnalyser.loadData(getId())
            .compose(disposeCompletable(getDisposable()))
            .subscribe(() -> {
                getView().showChart(true);
                chartAnalyser.showChart(getView().getChartView());
            }, throwable -> getView().showChart(false));
    }

    private Observable<Event> getEventSource(boolean forceReload) {
        if (!forceReload && event != null && isRotated())
            return Observable.just(event);
        else
            return eventRepository.getEvent(getId(), forceReload);
    }

    private Observable<Attendee> getAttendeeSource(boolean forceReload) {
        if (!forceReload && attendees != null && isRotated())
            return Observable.fromIterable(attendees);
        else
            return attendeeRepository.getAttendees(getId(), forceReload);
    }

    @VisibleForTesting
    public IEventDashboardView getView() {
        return super.getView();
    }

    @VisibleForTesting
    public Event getEvent() {
        return event;
    }

    @VisibleForTesting
    public void setEvent(Event event) {
        this.event = event;
    }
}
