package org.fossasia.openevent.app.module.main.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Erroneous;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.ItemResult;
import org.fossasia.openevent.app.common.data.models.Event;

public interface IMainView extends Erroneous, ItemResult<Event> {

    void setEventId(long eventId);

    void showEventList();

    void showDashboard();

    void onLogout();

}
