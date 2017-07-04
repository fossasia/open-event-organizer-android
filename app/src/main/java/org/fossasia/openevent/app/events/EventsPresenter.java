package org.fossasia.openevent.app.events;

import org.fossasia.openevent.app.data.contract.IEventRepository;
import org.fossasia.openevent.app.events.contract.IEventsPresenter;
import org.fossasia.openevent.app.events.contract.IEventsView;
import org.fossasia.openevent.app.utils.Utils;

import javax.inject.Inject;

import io.reactivex.schedulers.Schedulers;

public class EventsPresenter implements IEventsPresenter {

    private IEventsView eventsView;
    private IEventRepository eventsDataRepository;

    private boolean isListEmpty = true;

    @Inject
    public EventsPresenter(IEventRepository eventsDataRepository) {
        this.eventsDataRepository = eventsDataRepository;
    }

    @Override
    public void attach(IEventsView eventsView) {
        this.eventsView = eventsView;
    }

    @Override
    public void start() {
        loadUserEvents(false);
        loadOrganiser(false);
    }

    @Override
    public void detach() {
        eventsView = null;
    }

    private void hideProgress(boolean forceReload) {
        eventsView.showProgressBar(false);
        eventsView.showEmptyView(isListEmpty);

        if (forceReload)
            eventsView.onRefreshComplete();
    }

    @Override
    public void loadUserEvents(boolean forceReload) {
        if(eventsView == null)
            return;

        eventsView.showProgressBar(true);
        eventsView.showEmptyView(false);

        eventsDataRepository
            .getEvents(forceReload)
            .toSortedList()
            .subscribeOn(Schedulers.computation())
            .subscribe(events -> {
                if(eventsView == null)
                    return;
                eventsView.showEvents(events);
                isListEmpty = events.size() == 0;
                hideProgress(forceReload);
            }, throwable -> {
                if(eventsView == null)
                    return;

                eventsView.showEventError(throwable.getMessage());
                hideProgress(forceReload);
            });
    }

    /* Not dealing with progressbar here as main task is to show events */
    @Override
    public void loadOrganiser(boolean forceReload) {
        if(eventsView == null)
            return;

        eventsDataRepository.getOrganiser(false)
            .subscribe(user -> {
                if(eventsView == null)
                    return;

                String name = Utils.formatOptionalString("%s %s",
                    user.getUserDetail().getFirstName(),
                    user.getUserDetail().getLastName());

                eventsView.showOrganiserName(name.trim());
            }, throwable -> {
                if(eventsView == null)
                    return;
                eventsView.showOrganiserLoadError(throwable.getMessage());
            });
    }

    public IEventsView getView() {
        return eventsView;
    }

}
