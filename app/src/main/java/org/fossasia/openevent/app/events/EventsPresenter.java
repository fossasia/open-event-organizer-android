package org.fossasia.openevent.app.events;

import org.fossasia.openevent.app.data.contract.IEventRepository;
import org.fossasia.openevent.app.data.contract.ILoginModel;
import org.fossasia.openevent.app.events.contract.IEventsPresenter;
import org.fossasia.openevent.app.events.contract.IEventsView;
import org.fossasia.openevent.app.utils.Utils;

import javax.inject.Inject;

import timber.log.Timber;

public class EventsPresenter implements IEventsPresenter {

    private IEventsView eventsView;
    private IEventRepository eventsDataRepository;
    private ILoginModel loginModel;

    private boolean firstLoad = true;
    private boolean isListEmpty = true;

    @Inject
    public EventsPresenter(IEventRepository eventsDataRepository, ILoginModel loginModel) {
        this.eventsDataRepository = eventsDataRepository;
        this.loginModel = loginModel;
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
            .toList()
            .subscribe(events -> {
                if(eventsView == null)
                    return;

                eventsView.showEvents(events);
                if(eventsView.isTwoPane() && firstLoad)
                    eventsView.showInitialEvent();
                isListEmpty = events.size() == 0;
                hideProgress(forceReload);

                firstLoad = false;
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

    @Override
    public void logout() {
        if (eventsView == null)
            return;

        loginModel.logout()
            .subscribe(() -> {
                if (eventsView != null)
                    eventsView.onLogout();
            }, throwable -> {
                Timber.e(throwable);
                if (eventsView != null)
                    eventsView.showEventError(throwable.getMessage());
            });
    }

    public IEventsView getView() {
        return eventsView;
    }

}
