package org.fossasia.openevent.app.core.auth.login;

import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Successful;

import java.util.Set;

public interface LoginView extends Progressive, Successful, Erroneous {

    void attachEmails(Set<String> emails);

}
