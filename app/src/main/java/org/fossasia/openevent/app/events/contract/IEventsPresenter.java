package org.fossasia.openevent.app.events.contract;

public interface IEventsPresenter {

    void attach(IEventsView eventsView);

    void start();

    void detach();

    void loadUserEvents(boolean forceReload);

    void loadOrganiser(boolean forceReload);

}
