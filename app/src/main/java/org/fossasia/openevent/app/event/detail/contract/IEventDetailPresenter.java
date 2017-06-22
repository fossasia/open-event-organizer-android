package org.fossasia.openevent.app.event.detail.contract;

public interface IEventDetailPresenter {

    void attach(IEventDetailView eventDetailView, long initialEventId);

    void start(boolean forceStart);

    void detach();

    void loadTickets(long eventId, boolean forceReload);

    void loadAttendees(long eventId, boolean forceReload);

}
