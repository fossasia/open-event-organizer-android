package com.eventyay.organizer.core.share;

import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.ItemResult;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Refreshable;
import com.eventyay.organizer.common.mvp.view.Successful;
import com.eventyay.organizer.data.event.Event;

public interface ShareEventView extends Progressive, Erroneous, Successful, Refreshable, ItemResult<Event> {
}
