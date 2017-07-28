package org.fossasia.openevent.app.module.event.list;

import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.common.app.lifecycle.presenter.BasePresenter;
import org.fossasia.openevent.app.common.app.ContextManager;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.repository.contract.IEventRepository;
import org.fossasia.openevent.app.module.event.list.contract.IEventsView;
import org.fossasia.openevent.app.module.event.list.contract.IEventsPresenter;
import org.fossasia.openevent.app.common.utils.core.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.emptiable;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.progressiveErroneousRefresh;

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
    public void start() {
        loadUserEvents(false);
        loadOrganiser(false);
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
