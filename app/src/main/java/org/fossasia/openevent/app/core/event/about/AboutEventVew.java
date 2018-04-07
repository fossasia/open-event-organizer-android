package org.fossasia.openevent.app.core.event.about;

import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.ItemResult;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Refreshable;
import org.fossasia.openevent.app.data.copyright.Copyright;
import org.fossasia.openevent.app.data.event.Event;

public interface AboutEventVew extends Progressive, Erroneous, Refreshable, ItemResult<Event> {

    void setEventId(long id);

    void showCopyright(Copyright copyright);

    void changeCopyrightMenuItem(boolean creatingCopyright);

    void showCopyrightDeleted(String message);
}
