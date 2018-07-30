package com.eventyay.organizer.core.session.list;

import com.eventyay.organizer.common.mvp.view.Emptiable;
import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Refreshable;
import com.eventyay.organizer.data.session.Session;

public interface SessionsView extends Progressive, Erroneous, Refreshable, Emptiable<Session> {

    void showMessage(String message);

    void changeToolbarMode(boolean editMode, boolean deleteMode);

    void openUpdateSessionFragment(long sessionId);

    void exitContextualMenuMode();

    void enterContextualMenuMode();
}
