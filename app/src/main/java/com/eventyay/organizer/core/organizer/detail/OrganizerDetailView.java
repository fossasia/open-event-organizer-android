package com.eventyay.organizer.core.organizer.detail;

import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.ItemResult;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Refreshable;
import com.eventyay.organizer.data.user.User;

public interface OrganizerDetailView extends Progressive, Erroneous, Refreshable, ItemResult<User> { }
