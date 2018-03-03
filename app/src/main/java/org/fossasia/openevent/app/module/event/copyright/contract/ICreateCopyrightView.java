package org.fossasia.openevent.app.module.event.copyright.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Erroneous;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Successful;

public interface ICreateCopyrightView extends Erroneous, Successful {

    void dismiss();
}
