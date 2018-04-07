package org.fossasia.openevent.app.core.event.list;

import org.fossasia.openevent.app.common.mvp.view.Emptiable;
import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Refreshable;
import org.fossasia.openevent.app.data.event.Event;

public interface EventsView extends Progressive, Erroneous, Refreshable, Emptiable<Event> { }
