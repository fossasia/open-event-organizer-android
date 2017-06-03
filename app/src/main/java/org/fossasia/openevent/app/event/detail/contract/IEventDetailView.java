package org.fossasia.openevent.app.event.detail.contract;

import org.fossasia.openevent.app.data.models.Event;

public interface IEventDetailView {

    void showProgressBar(boolean show);

    void showEvent(Event event);

    void showEventLoadError(String error);

}
