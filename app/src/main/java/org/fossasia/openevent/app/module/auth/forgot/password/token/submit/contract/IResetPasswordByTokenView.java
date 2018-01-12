package org.fossasia.openevent.app.module.auth.forgot.password.token.submit.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Erroneous;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Progressive;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Successful;

public interface IResetPasswordByTokenView extends Progressive, Successful, Erroneous {
}
