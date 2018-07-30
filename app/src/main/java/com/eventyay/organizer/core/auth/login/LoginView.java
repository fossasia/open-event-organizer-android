package com.eventyay.organizer.core.auth.login;

import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Successful;

import java.util.Set;

public interface LoginView extends Progressive, Successful, Erroneous {

    void attachEmails(Set<String> emails);

}
