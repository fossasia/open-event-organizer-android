package com.eventyay.organizer.core.organizer.detail;

import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Refreshable;
import com.eventyay.organizer.data.user.User;

public interface OrganizerDetailView extends Progressive, Erroneous, Refreshable {

    void showSnackbar(String message);

    void onSuccess(String message);

    void setUser(User user);
}
