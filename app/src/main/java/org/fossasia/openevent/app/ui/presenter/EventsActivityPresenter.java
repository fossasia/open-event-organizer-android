package org.fossasia.openevent.app.ui.presenter;

import org.fossasia.openevent.app.contract.model.IEventDataRepository;
import org.fossasia.openevent.app.contract.model.IUtilModel;
import org.fossasia.openevent.app.contract.presenter.IEventListPresenter;
import org.fossasia.openevent.app.contract.view.IEventListView;
import org.fossasia.openevent.app.utils.Utils;

public class EventsActivityPresenter implements IEventListPresenter {

    private IEventListView eventListView;
    private IEventDataRepository eventRepository;
    private IUtilModel utilModel;

    public EventsActivityPresenter(IEventListView eventListView, IEventDataRepository eventRepository, IUtilModel utilModel) {
        this.eventListView = eventListView;
        this.eventRepository = eventRepository;
        this.utilModel = utilModel;
    }

    @Override
    public void attach() {
        loadUserEvents(false);
        loadOrganiser(false);
    }

    @Override
    public void detach() {
        eventListView = null;
    }

    @Override
    public void loadUserEvents(boolean forceReload) {
        if(eventListView == null)
            return;

        eventListView.showProgressBar(true);

        eventRepository
            .getEvents(forceReload)
            .subscribe(events -> {
                if(eventListView == null)
                    return;
                eventListView.showEvents(events);
                eventListView.showProgressBar(false);
            }, throwable -> {
                if(eventListView == null)
                    return;
                eventListView.showEventError(throwable.getMessage());
                eventListView.showProgressBar(false);
            });
    }

    /* Not dealing with progressbar here as main task is to show events */
    @Override
    public void loadOrganiser(boolean forceReload) {
        if(eventListView == null)
            return;

        eventRepository.getOrganiser(false)
            .subscribe(user -> {
                if(eventListView == null)
                    return;

                String name = Utils.formatOptionalString("%s %s",
                    user.getUserDetail().getFirstName(),
                    user.getUserDetail().getLastName());

                eventListView.showOrganiserName(name.trim());
                eventListView.showOrganiserPanel(true);
            }, throwable -> {
                if(eventListView == null)
                    return;
                eventListView.showOrganiserLoadError(throwable.getMessage());
                eventListView.showOrganiserPanel(false);
            });
    }

    @Override
    public void logout() {
        utilModel.logout();
        if(eventListView != null)
            eventListView.onLogout();
    }

    public IEventListView getView() {
        return eventListView;
    }

}
