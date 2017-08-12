package org.fossasia.openevent.app.module.event.about.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.view.ItemResult;
import org.fossasia.openevent.app.common.data.models.Event;

public interface IAboutEventVew extends ItemResult<Event> {

    void setEventId(long id);

}
