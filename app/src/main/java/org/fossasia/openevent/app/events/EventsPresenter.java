package org.fossasia.openevent.app.events;

import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.common.BasePresenter;
import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.repository.contract.IEventRepository;
import org.fossasia.openevent.app.events.contract.IEventsPresenter;
import org.fossasia.openevent.app.events.contract.IEventsView;
import org.fossasia.openevent.app.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.emptiable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousRefresh;

public class EventsPresenter extends BasePresenter<IEventsView> implements IEventsPresenter {

    private final List<Event> events = new ArrayList<>();

    private final IEventRepository eventsDataRepository;
    private final ContextManager contextManager;

    @Inject
    public EventsPresenter(IEventRepository eventsDataRepository, ContextManager contextManager) {
        this.eventsDataRepository = eventsDataRepository;
        this.contextManager = contextManager;
    }

    @Override
    public void attach(IEventsView eventsView) {
        super.attach(eventsView);
    }

    @Override
    public void start() {
        loadUserEvents(false);
        loadOrganiser(false);
    }

    @Override
    public void detach() {
        super.detach();
    }

    @Override
    public List<Event> getEvents() {
        return events;
    }

    @Override
    public void loadUserEvents(boolean forceReload) {
        if(getView() == null)
            return;

        eventsDataRepository
            .getEvents(forceReload)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneousRefresh(getView(), forceReload))
            .toSortedList()
            .compose(emptiable(getView(), events))
            .subscribe(Logger::logSuccess, Logger::logError);
    }

    /* Not dealing with progressbar here as main task is to show events */
    @Override
    public void loadOrganiser(boolean forceReload) {
        if(getView() == null)
            return;

        eventsDataRepository.getOrganiser(false)
            .compose(dispose(getDisposable()))
            .doOnError(Logger::logError)
            .subscribe(user -> {
                contextManager.setOrganiser(user);

                String name = Utils.formatOptionalString("%s %s",
                    user.getFirstName(),
                    user.getLastName());

                getView().showOrganiserName(name.trim());
            }, throwable -> getView().showOrganiserLoadError(throwable.getMessage()));
    }

    @VisibleForTesting
    public IEventsView getView() {
        return super.getView();
    }

}
