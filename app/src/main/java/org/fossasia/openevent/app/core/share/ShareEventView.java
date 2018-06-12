package org.fossasia.openevent.app.core.share;

import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.ItemResult;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Refreshable;
import org.fossasia.openevent.app.common.mvp.view.Successful;
import org.fossasia.openevent.app.data.event.Event;

public interface ShareEventView extends Progressive, Erroneous, Successful, Refreshable, ItemResult<Event> {
}
