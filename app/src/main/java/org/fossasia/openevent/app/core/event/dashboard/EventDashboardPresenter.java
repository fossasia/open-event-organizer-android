package org.fossasia.openevent.app.core.event.dashboard;

import android.support.annotation.VisibleForTesting;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.presenter.AbstractDetailPresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.core.event.dashboard.analyser.ChartAnalyser;
import org.fossasia.openevent.app.core.event.dashboard.analyser.TicketAnalyser;
import org.fossasia.openevent.app.data.ContextUtils;
import org.fossasia.openevent.app.data.attendee.Attendee;
import org.fossasia.openevent.app.data.attendee.AttendeeRepository;
import org.fossasia.openevent.app.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.data.db.DbFlowDatabaseChangeListener;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.event.EventRepository;
import org.fossasia.openevent.app.data.event.EventStatistics;
import org.fossasia.openevent.app.utils.Utils;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneous;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousRefresh;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.result;

public class EventDashboardPresenter extends AbstractDetailPresenter<Long, EventDashboardView> {

    private Event event;
    private List<Attendee> attendees;
    private EventStatistics eventStatistics;
    private final EventRepository eventRepository;
    private final AttendeeRepository attendeeRepository;
    private final TicketAnalyser ticketAnalyser;
    private final ChartAnalyser chartAnalyser;
    private final ContextUtils utilModel;
    private final DatabaseChangeListener<Event> eventChangeListener;

    @Inject
    public EventDashboardPresenter(EventRepository eventRepository, AttendeeRepository attendeeRepository,
                                   TicketAnalyser ticketAnalyser, ChartAnalyser chartAnalyser, ContextUtils utilModel,
                                   DatabaseChangeListener<Event> eventChangeListener) {
        this.eventRepository = eventRepository;
        this.ticketAnalyser = ticketAnalyser;
        this.attendeeRepository = attendeeRepository;
        this.chartAnalyser = chartAnalyser;
        this.utilModel = utilModel;
        this.eventChangeListener = eventChangeListener;
    }

    @Override
    public void start() {
        loadDetails(false);
        listenChanges();
    }

    public void loadDetails(boolean forceReload) {
        if (getView() == null)
            return;

        loadSalesChart();
        loadCheckInTimesChart();
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
                    loadSalesChart();
                    loadCheckInTimesChart();
                }
            }, Logger::logError);

        loadEventStatistics(forceReload);
    }

    private void listenChanges() {
        eventChangeListener.startListening();
        eventChangeListener.getNotifier()
            .compose(dispose(getDisposable()))
            .map(DbFlowDatabaseChangeListener.ModelChange::getAction)
            .filter(action -> action.equals(BaseModel.Action.UPDATE))
            .subscribeOn(Schedulers.io())
            .subscribe(speakersCallModelChange -> loadDetails(false), Logger::logError);
    }

    public void confirmToggle() {
        if (Event.STATE_PUBLISHED.equals(event.state)) {
            getView().switchEventState();
            getView().showEventUnpublishDialog();
        } else if (Utils.isEmpty(event.getLocationName())) {
            getView().switchEventState();
            getView().showEventLocationDialog();
        } else {
            toggleState();
        }
    }

    public void toggleState() {
        event.state = Event.STATE_DRAFT.equals(event.state) ? Event.STATE_PUBLISHED : Event.STATE_DRAFT;
        eventRepository.updateEvent(event)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .doFinally(() -> getView().showResult(event))
            .subscribe(updatedEvent -> {
                event.state = updatedEvent.state;
                if (Event.STATE_PUBLISHED.equals(event.state)) {
                    getView().showEventShareDialog();
                } else {
                    getView().onSuccess(utilModel.getResourceString(R.string.draft_success));
                }
            },
            throwable -> event.state = Event.STATE_DRAFT.equals(event.state) ? Event.STATE_PUBLISHED : Event.STATE_DRAFT);
    }

    private void loadEventStatistics(boolean forceReload) {
        if (!forceReload && isRotated() && eventStatistics != null)
            getView().showStatistics(eventStatistics);
        else {
            eventRepository.getEventStatistics(getId())
                .compose(dispose(getDisposable()))
                .compose(progressiveErroneousRefresh(getView(), forceReload))
                .doFinally(() -> getView().showStatistics(eventStatistics))
                .subscribe(statistics -> eventStatistics = statistics, Logger::logError);
        }
    }

    private void loadSalesChart() {
        chartAnalyser.showChart(getView().getSalesChartView());
        chartAnalyser.loadData(getId())
            .compose(disposeCompletable(getDisposable()))
            .subscribe(() -> {
                getView().showChartSales(true);
                chartAnalyser.showChart(getView().getSalesChartView());
            }, throwable -> getView().showChartSales(false));
    }

    private void loadCheckInTimesChart() {
        chartAnalyser.showChart(getView().getCheckinTimeChartView());
        chartAnalyser.loadDataCheckIn(getId())
            .compose(disposeCompletable(getDisposable()))
            .subscribe(() -> {
                getView().showChartCheckIn(true);
                chartAnalyser.showChart(getView().getCheckinTimeChartView());
            }, throwable -> getView().showChartCheckIn(false));
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
    public EventDashboardView getView() {
        return super.getView();
    }

    @VisibleForTesting
    public Event getEvent() {
        return event;
    }

    public EventStatistics getEventStatistics() {
        return eventStatistics;
    }

    @VisibleForTesting
    public void setEvent(Event event) {
        this.event = event;
    }
}
