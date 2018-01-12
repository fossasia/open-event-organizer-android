package org.fossasia.openevent.app.module.auth.forgot.password.token.request.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Erroneous;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Progressive;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Successful;

import java.util.Set;

public interface IForgotPasswordView extends Progressive, Successful, Erroneous {

    void attachEmails(Set<String> emails);
}
