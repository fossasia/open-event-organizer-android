package org.fossasia.openevent.app.core.auth.reset;

import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Successful;

public interface ResetPasswordView extends Progressive, Successful, Erroneous {

    void showMessage(String message);

}
