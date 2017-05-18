package org.fossasia.openevent.app.contract.presenter;

public interface IEventListPresenter {

    void attach();

    void detach();

    void loadUserEvents(boolean forceReload);

    void loadOrganiser(boolean forceReload);

    void logout();

}
