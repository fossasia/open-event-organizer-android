package org.fossasia.openevent.app.event.detail.contract;

import org.fossasia.openevent.app.data.models.Event;

public interface IEventDetailPresenter {

    void attach(IEventDetailView eventDetailView, Event initialEvent);

    void start();

    void detach();

    void loadTickets(long eventId, boolean forceReload);

    void loadAttendees(long eventId, boolean forceReload);

}
