package org.fossasia.openevent.app.module.faq.create.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Erroneous;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Progressive;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Successful;

public interface ICreateFaqView extends Progressive, Erroneous, Successful {

    void dismiss();

}
