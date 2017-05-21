package org.fossasia.openevent.app.contract.presenter;

public interface IEventDetailPresenter {

    void attach();

    void detach();

    void loadTickets(long eventId, boolean forceReload);

    void loadAttendees(long eventId, boolean forceReload);

}
