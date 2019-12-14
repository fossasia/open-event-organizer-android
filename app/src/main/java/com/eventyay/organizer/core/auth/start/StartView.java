package com.eventyay.organizer.core.auth.start;

import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;

import java.util.Set;

public interface StartView extends Progressive, Erroneous {

    void attachEmails(Set<String> emails);

    void handleIntent();

    void toNextAuthFragment(boolean isEmailRegistered);
}
