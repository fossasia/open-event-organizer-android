package com.eventyay.organizer.core.event.list;

import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Refreshable;

public interface EventsView extends Progressive, Erroneous, Refreshable {}
