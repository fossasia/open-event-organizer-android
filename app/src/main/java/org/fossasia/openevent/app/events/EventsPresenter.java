package org.fossasia.openevent.app.events;

import org.fossasia.openevent.app.data.contract.IEventDataRepository;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.events.contract.IEventsPresenter;
import org.fossasia.openevent.app.events.contract.IEventsView;
import org.fossasia.openevent.app.utils.Utils;

public class EventsPresenter implements IEventsPresenter {

    private IEventsView eventsView;
    private IEventDataRepository eventsDataRepository;
    private IUtilModel utilModel;

    public EventsPresenter(IEventsView eventsView, IEventDataRepository eventsDataRepository, IUtilModel utilModel) {
        this.eventsView = eventsView;
        this.eventsDataRepository = eventsDataRepository;
        this.utilModel = utilModel;
    }

    @Override
    public void attach() {
        loadUserEvents(false);
        loadOrganiser(false);
    }

    @Override
    public void detach() {
        eventsView = null;
    }

    @Override
    public void loadUserEvents(boolean forceReload) {
        if(eventsView == null)
            return;

        eventsView.showProgressBar(true);

        eventsDataRepository
            .getEvents(forceReload)
            .subscribe(events -> {
                if(eventsView == null)
                    return;
                eventsView.showEvents(events);
                eventsView.showProgressBar(false);
            }, throwable -> {
                if(eventsView == null)
                    return;
                eventsView.showEventError(throwable.getMessage());
                eventsView.showProgressBar(false);
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
                eventsView.showOrganiserPanel(true);
            }, throwable -> {
                if(eventsView == null)
                    return;
                eventsView.showOrganiserLoadError(throwable.getMessage());
                eventsView.showOrganiserPanel(false);
            });
    }

    @Override
    public void logout() {
        utilModel.logout();
        if(eventsView != null)
            eventsView.onLogout();
    }

    public IEventsView getView() {
        return eventsView;
    }

}
