package org.fossasia.openevent.app.module.event.create.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Erroneous;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Successful;

public interface ICreateEventView extends Erroneous, Successful {

    void dismiss();
}

