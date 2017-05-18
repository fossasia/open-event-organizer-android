package org.fossasia.openevent.app.contract.view;

import org.fossasia.openevent.app.data.models.Event;

import java.util.List;

public interface IEventListView {

    void showProgressBar(boolean show);

    void showEvents(List<Event> events);

    void showEventError(String error);

    void onLogout();

}
