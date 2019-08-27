package com.eventyay.organizer.core.event.dashboard;

import static com.eventyay.organizer.common.rx.ViewTransformers.dispose;
import static com.eventyay.organizer.common.rx.ViewTransformers.disposeCompletable;
import static com.eventyay.organizer.common.rx.ViewTransformers.progressiveErroneous;
import static com.eventyay.organizer.common.rx.ViewTransformers.progressiveErroneousRefresh;
import static com.eventyay.organizer.common.rx.ViewTransformers.result;

import androidx.annotation.VisibleForTesting;
import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.presenter.AbstractDetailPresenter;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.core.event.dashboard.analyser.ChartAnalyser;
import com.eventyay.organizer.core.event.dashboard.analyser.TicketAnalyser;
import com.eventyay.organizer.data.ContextUtils;
import com.eventyay.organizer.data.Preferences;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.data.attendee.AttendeeRepository;
import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.db.DbFlowDatabaseChangeListener;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.event.EventRepository;
import com.eventyay.organizer.data.event.EventStatistics;
import com.eventyay.organizer.data.order.OrderRepository;
import com.eventyay.organizer.data.order.OrderStatistics;
import com.eventyay.organizer.utils.Utils;
import com.raizlabs.android.dbflow.structure.BaseModel;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import javax.inject.Inject;

public class EventDashboardPresenter extends AbstractDetailPresenter<Long, EventDashboardView> {

    private static final String DEVELOPER_MODE_KEY = "developer_mode";
    private final EventRepository eventRepository;
    private final OrderRepository orderRepository;
    private final AttendeeRepository attendeeRepository;
    private final TicketAnalyser ticketAnalyser;
    private final ChartAnalyser chartAnalyser;
    private final ContextUtils utilModel;
    private final DatabaseChangeListener<Event> eventChangeListener;
    private final Preferences sharedPreferenceModel;
    private Event event;
    private List<Attendee> attendees;
    private EventStatistics eventStatistics;
    private OrderStatistics orderStatistics;

    @Inject
    public EventDashboardPresenter(
            EventRepository eventRepository,
            AttendeeRepository attendeeRepository,
            OrderRepository orderRepository,
            TicketAnalyser ticketAnalyser,
            ChartAnalyser chartAnalyser,
            ContextUtils utilModel,
            DatabaseChangeListener<Event> eventChangeListener,
            Preferences sharedPreferenceModel) {
        this.eventRepository = eventRepository;
        this.ticketAnalyser = ticketAnalyser;
        this.attendeeRepository = attendeeRepository;
        this.chartAnalyser = chartAnalyser;
        this.utilModel = utilModel;
        this.eventChangeListener = eventChangeListener;
        this.orderRepository = orderRepository;
        this.sharedPreferenceModel = sharedPreferenceModel;
    }

    @Override
    public void start() {
        loadDetails(false);
        listenChanges();
    }

    public void loadDetails(boolean forceReload) {
        if (getView() == null) return;

        boolean isDeveloperModeEnabled =
                sharedPreferenceModel.getBoolean(DEVELOPER_MODE_KEY, false);

        if (isDeveloperModeEnabled) getView().showDeveloperModeFeatures();

        loadSalesChart();
        loadCheckInTimesChart();
        getEventSource(forceReload)
                .compose(dispose(getDisposable()))
                .compose(result(getView()))
                .flatMap(
                        loadedEvent -> {
                            this.event = loadedEvent;
                            ticketAnalyser.analyseTotalTickets(event);
                            return getAttendeeSource(forceReload);
                        })
                .compose(progressiveErroneousRefresh(getView(), forceReload))
                .toList()
                .subscribe(
                        attendees -> {
                            this.attendees = attendees;
                            ticketAnalyser.analyseSoldTickets(event, attendees);

                            if (forceReload) {
                                chartAnalyser.reset();
                                loadSalesChart();
                                loadCheckInTimesChart();
                            }
                        },
                        Logger::logError);

        loadEventStatistics(forceReload);
        loadOrderStatistics(forceReload);
    }

    private void listenChanges() {
        eventChangeListener.startListening();
        eventChangeListener
                .getNotifier()
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
        } else if (Utils.isEmpty(event.getLocationName()) && !event.isEventOnline) {
            getView().switchEventState();
            getView().showEventLocationDialog();
        } else {
            toggleState();
        }
    }

    public void toggleState() {
        event.state =
                Event.STATE_DRAFT.equals(event.state) ? Event.STATE_PUBLISHED : Event.STATE_DRAFT;
        eventRepository
                .updateEvent(event)
                .compose(dispose(getDisposable()))
                .compose(progressiveErroneous(getView()))
                .doFinally(() -> getView().showResult(event))
                .subscribe(
                        updatedEvent -> {
                            event.state = updatedEvent.state;
                            if (Event.STATE_PUBLISHED.equals(event.state)) {
                                getView().showEventShareDialog();
                            } else {
                                getView()
                                        .onSuccess(
                                                utilModel.getResourceString(
                                                        R.string.draft_success));
                            }
                        },
                        throwable ->
                                event.state =
                                        Event.STATE_DRAFT.equals(event.state)
                                                ? Event.STATE_PUBLISHED
                                                : Event.STATE_DRAFT);
    }

    private void loadEventStatistics(boolean forceReload) {
        if (!forceReload && isRotated() && eventStatistics != null)
            getView().showStatistics(eventStatistics);
        else {
            eventRepository
                    .getEventStatistics(getId())
                    .compose(dispose(getDisposable()))
                    .compose(progressiveErroneousRefresh(getView(), forceReload))
                    .doFinally(() -> getView().showStatistics(eventStatistics))
                    .subscribe(statistics -> eventStatistics = statistics, Logger::logError);
        }
    }

    private void loadOrderStatistics(boolean forceReload) {
        if (!forceReload && isRotated() && orderStatistics != null)
            getView().showOrderStatistics(orderStatistics);
        else {
            orderRepository
                    .getOrderStatisticsForEvent(getId(), forceReload)
                    .compose(dispose(getDisposable()))
                    .compose(progressiveErroneousRefresh(getView(), forceReload))
                    .doFinally(() -> getView().showOrderStatistics(orderStatistics))
                    .subscribe(statistics -> orderStatistics = statistics, Logger::logError);
        }
    }

    private void loadSalesChart() {
        chartAnalyser.showChart(getView().getSalesChartView());
        chartAnalyser
                .loadData(getId())
                .compose(disposeCompletable(getDisposable()))
                .subscribe(
                        () -> {
                            getView().showChartSales(true);
                            chartAnalyser.showChart(getView().getSalesChartView());
                        },
                        throwable -> getView().showChartSales(false));
    }

    private void loadCheckInTimesChart() {
        chartAnalyser.showChart(getView().getCheckinTimeChartView());
        chartAnalyser
                .loadDataCheckIn(getId())
                .compose(disposeCompletable(getDisposable()))
                .subscribe(
                        () -> {
                            getView().showChartCheckIn(true);
                            chartAnalyser.showChart(getView().getCheckinTimeChartView());
                        },
                        throwable -> getView().showChartCheckIn(false));
    }

    private Observable<Event> getEventSource(boolean forceReload) {
        if (!forceReload && event != null && isRotated()) return Observable.just(event);
        else return eventRepository.getEvent(getId(), forceReload);
    }

    private Observable<Attendee> getAttendeeSource(boolean forceReload) {
        if (!forceReload && attendees != null && isRotated())
            return Observable.fromIterable(attendees);
        else return attendeeRepository.getAttendees(getId(), forceReload);
    }

    @VisibleForTesting
    public EventDashboardView getView() {
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

    public EventStatistics getEventStatistics() {
        return eventStatistics;
    }
}
