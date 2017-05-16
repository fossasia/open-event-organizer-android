package org.fossasia.openevent.app.contract.presenter;

public interface EventListPresenter {

    void attach();

    void detach();

    void loadUserEvents(boolean forceReload);

    void logout();

}
