package org.fossasia.openevent.app.main.contract;

import org.fossasia.openevent.app.data.models.Event;

public interface IMainView {

    void loadDashboard(long eventId);

    void showEvent(Event event);

    void onLogout();

    void showError(String error);
}
