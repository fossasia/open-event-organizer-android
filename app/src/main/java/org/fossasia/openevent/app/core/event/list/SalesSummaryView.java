package org.fossasia.openevent.app.core.event.list;

import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.ItemResult;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.data.event.Event;

public interface SalesSummaryView extends Progressive, Erroneous, ItemResult<Event> {
}
