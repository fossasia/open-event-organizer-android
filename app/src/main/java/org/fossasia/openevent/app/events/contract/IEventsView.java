package org.fossasia.openevent.app.events.contract;

import org.fossasia.openevent.app.common.contract.view.Emptiable;
import org.fossasia.openevent.app.common.contract.view.Erroneous;
import org.fossasia.openevent.app.common.contract.view.Progressive;
import org.fossasia.openevent.app.common.contract.view.Refreshable;
import org.fossasia.openevent.app.data.models.Event;

public interface IEventsView extends Progressive, Erroneous, Refreshable, Emptiable<Event> {

    void showOrganiserName(String name);

    void showOrganiserLoadError(String error);

}
