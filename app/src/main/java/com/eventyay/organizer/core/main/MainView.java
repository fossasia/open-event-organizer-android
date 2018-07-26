package com.eventyay.organizer.core.main;

import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.ItemResult;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.user.User;

public interface MainView extends Erroneous, ItemResult<Event> {

    void setEventId(long eventId);

    void showEventList();

    void showDashboard();

    void showOrganizer(User organizer);

    void invalidateDateViews();

    void onLogout();

}
