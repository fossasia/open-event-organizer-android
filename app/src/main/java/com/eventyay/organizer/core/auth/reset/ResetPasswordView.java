package com.eventyay.organizer.core.auth.reset;

import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Successful;

public interface ResetPasswordView extends Progressive, Successful, Erroneous {

    void showMessage(String message);

}
