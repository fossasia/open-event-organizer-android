package org.fossasia.openevent.app.core.organizer.detail;

import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.ItemResult;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Refreshable;
import org.fossasia.openevent.app.data.models.User;

public interface IOrganizerDetailView extends Progressive, Erroneous, Refreshable, ItemResult<User> { }
