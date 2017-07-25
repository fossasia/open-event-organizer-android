package org.fossasia.openevent.app.module.event.list.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Emptiable;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Erroneous;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Progressive;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Refreshable;
import org.fossasia.openevent.app.common.data.models.Event;

public interface IEventsView extends Progressive, Erroneous, Refreshable, Emptiable<Event> {

    void showOrganiserName(String name);

    void showOrganiserLoadError(String error);

}
