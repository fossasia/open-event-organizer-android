package com.eventyay.organizer.core.auth.login;

import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Successful;

public interface LoginView extends Progressive, Successful, Erroneous {

    void openResetPasswordFragment(boolean resetPassword);
}
