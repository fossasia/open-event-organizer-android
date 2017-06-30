package org.fossasia.openevent.app.event.detail.contract;

public interface IEventDetailPresenter {

    void attach(IEventDetailView eventDetailView, long initialEventId);

    void start();

    void refresh();

    void detach();

    void loadEvent(long eventId, boolean forceReload);

    void loadAttendees(long eventId, boolean forceReload);

}
