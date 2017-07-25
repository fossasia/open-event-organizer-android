package org.fossasia.openevent.app.module.event.dashboard;

import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.common.app.lifecycle.presenter.BaseDetailPresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.repository.contract.IAttendeeRepository;
import org.fossasia.openevent.app.common.data.repository.contract.IEventRepository;
import org.fossasia.openevent.app.module.event.dashboard.analyser.ChartAnalyser;
import org.fossasia.openevent.app.module.event.dashboard.analyser.TicketAnalyser;
import org.fossasia.openevent.app.module.event.dashboard.contract.IEventDashboardView;
import org.fossasia.openevent.app.module.event.dashboard.contract.IEventDashboardPresenter;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.progressiveErroneousRefresh;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.result;

public class EventDashboardPresenter extends BaseDetailPresenter<Long, IEventDashboardView> implements IEventDashboardPresenter {

    private Event event;
    private final IEventRepository eventRepository;
    private final IAttendeeRepository attendeeRepository;
    private final TicketAnalyser ticketAnalyser;
    private final ChartAnalyser chartAnalyser;

    @Inject
    public EventDashboardPresenter(IEventRepository eventRepository, IAttendeeRepository attendeeRepository, TicketAnalyser ticketAnalyser, ChartAnalyser chartAnalyser) {
        this.eventRepository = eventRepository;
        this.ticketAnalyser = ticketAnalyser;
        this.attendeeRepository = attendeeRepository;
        this.chartAnalyser = chartAnalyser;
    }

    @Override
    public void attach(Long eventId, IEventDashboardView eventDetailView) {
        super.attach(eventId, eventDetailView);
    }

    @Override
    public void start() {
        loadDetails(false);
    }

    @Override
    public void detach() {
        super.detach();
    }

    @Override
    public void loadDetails(boolean forceReload) {
        if (getView() == null)
            return;

        chartAnalyser.loadData(getId())
            .compose(disposeCompletable(getDisposable()))
            .subscribe(() -> {
                getView().showChart(true);
                chartAnalyser.showChart(getView().getChartView());
            }, throwable -> getView().showChart(false));

        eventRepository
            .getEvent(getId(), forceReload)
            .compose(dispose(getDisposable()))
            .compose(result(getView()))
            .flatMap(loadedEvent -> {
                this.event = loadedEvent;

                ticketAnalyser.analyseTotalTickets(event);

                return attendeeRepository.getAttendees(getId(), forceReload);
            })
            .compose(progressiveErroneousRefresh(getView(), forceReload))
            .toList()
            .subscribe(attendees -> ticketAnalyser.analyseSoldTickets(event, attendees), Logger::logError);
    }

    @VisibleForTesting
    public IEventDashboardView getView() {
        return super.getView();
    }

    @VisibleForTesting
    public Event getEvent() {
        return event;
    }
}
