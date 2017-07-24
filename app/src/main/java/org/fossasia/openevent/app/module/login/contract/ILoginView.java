package org.fossasia.openevent.app.module.login.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Erroneous;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Progressive;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Successful;

import java.util.Set;

public interface ILoginView extends Progressive, Successful, Erroneous {

    void attachEmails(Set<String> emails);

}
