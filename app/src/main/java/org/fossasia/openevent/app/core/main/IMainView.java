package org.fossasia.openevent.app.core.main;

import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.ItemResult;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.User;

public interface IMainView extends Erroneous, ItemResult<Event> {

    void setEventId(long eventId);

    void showEventList();

    void showDashboard();

    void showOrganizer(User organizer);

    void invalidateDateViews();

    void onLogout();

}
