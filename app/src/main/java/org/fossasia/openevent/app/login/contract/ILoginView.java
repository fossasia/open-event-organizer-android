package org.fossasia.openevent.app.login.contract;

import org.fossasia.openevent.app.common.contract.view.Erroneous;
import org.fossasia.openevent.app.common.contract.view.Progressive;
import org.fossasia.openevent.app.common.contract.view.Successful;

import java.util.Set;

public interface ILoginView extends Progressive, Successful, Erroneous {

    void attachEmails(Set<String> emails);

}
