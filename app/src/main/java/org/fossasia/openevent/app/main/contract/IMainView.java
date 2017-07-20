package org.fossasia.openevent.app.main.contract;

import org.fossasia.openevent.app.common.contract.view.Erroneous;
import org.fossasia.openevent.app.common.contract.view.ItemResult;
import org.fossasia.openevent.app.data.models.Event;

public interface IMainView extends Erroneous, ItemResult<Event> {

    void loadInitialPage(long eventId);

    void onLogout();

}
