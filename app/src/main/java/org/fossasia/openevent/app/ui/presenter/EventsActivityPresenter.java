package org.fossasia.openevent.app.ui.presenter;

import org.fossasia.openevent.app.contract.model.EventModel;
import org.fossasia.openevent.app.contract.model.UtilModel;
import org.fossasia.openevent.app.contract.presenter.EventListPresenter;
import org.fossasia.openevent.app.contract.view.EventListView;

public class EventsActivityPresenter implements EventListPresenter {

    private EventListView eventListView;
    private EventModel eventModel;
    private UtilModel utilModel;

    public EventsActivityPresenter(EventListView eventListView, EventModel eventModel, UtilModel utilModel) {
        this.eventListView = eventListView;
        this.eventModel = eventModel;
        this.utilModel = utilModel;
    }

    @Override
    public void attach() {
        loadUserEvents(false);
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

        eventModel
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

    @Override
    public void logout() {
        utilModel.logout();
        eventListView.onLogout();
    }

    public EventListView getView() {
        return eventListView;
    }

}
