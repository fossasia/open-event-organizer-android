package org.fossasia.openevent.app.event.detail.contract;


public interface IEventDetailPresenter {

    void attach();

    void detach();

    void loadTickets(long eventId, boolean forceReload);

    void loadAttendees(long eventId, boolean forceReload);

}
