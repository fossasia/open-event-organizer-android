package org.fossasia.openevent.app.ui.presenter;

import org.fossasia.openevent.app.contract.model.IEventModel;
import org.fossasia.openevent.app.contract.model.IUtilModel;
import org.fossasia.openevent.app.contract.presenter.IEventListPresenter;
import org.fossasia.openevent.app.contract.view.IEventListView;

public class EventsActivityPresenter implements IEventListPresenter {

    private IEventListView eventListView;
    private IEventModel eventModel;
    private IUtilModel utilModel;

    public EventsActivityPresenter(IEventListView eventListView, IEventModel eventModel, IUtilModel utilModel) {
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

    public IEventListView getView() {
        return eventListView;
    }

}
