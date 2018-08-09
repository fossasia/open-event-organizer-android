package com.eventyay.organizer.core.event.about;

import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.ItemResult;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Refreshable;
import com.eventyay.organizer.data.copyright.Copyright;
import com.eventyay.organizer.data.event.Event;

public interface AboutEventVew extends Progressive, Erroneous, Refreshable, ItemResult<Event> {

    void setEventId(long id);

    void showCopyright(Copyright copyright);

    void changeCopyrightMenuItem(boolean creatingCopyright);

    void showCopyrightDeleted(String message);
}
