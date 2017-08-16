package org.fossasia.openevent.app.module.organizer.detail.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Erroneous;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.ItemResult;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Progressive;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Refreshable;
import org.fossasia.openevent.app.common.data.models.User;

public interface IOrganizerDetailView extends Progressive, Erroneous, Refreshable, ItemResult<User> { }
