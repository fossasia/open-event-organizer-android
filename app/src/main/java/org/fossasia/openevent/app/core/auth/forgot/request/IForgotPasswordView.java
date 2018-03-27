package org.fossasia.openevent.app.core.auth.forgot.request;

import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Successful;

import java.util.Set;

public interface IForgotPasswordView extends Progressive, Successful, Erroneous {

    void attachEmails(Set<String> emails);
}
