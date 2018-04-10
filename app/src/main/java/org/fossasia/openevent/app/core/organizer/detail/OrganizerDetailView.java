package org.fossasia.openevent.app.core.organizer.detail;

import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.ItemResult;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Refreshable;
import org.fossasia.openevent.app.data.user.User;

public interface OrganizerDetailView extends Progressive, Erroneous, Refreshable, ItemResult<User> { }
