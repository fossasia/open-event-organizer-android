package org.fossasia.openevent.app.events.contract;

public interface IEventsPresenter {

    void attach();

    void detach();

    void loadUserEvents(boolean forceReload);

    void loadOrganiser(boolean forceReload);

    void logout();

}
