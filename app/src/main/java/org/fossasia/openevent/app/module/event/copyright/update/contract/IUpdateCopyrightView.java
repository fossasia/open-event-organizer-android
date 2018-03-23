package org.fossasia.openevent.app.module.event.copyright.update.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Erroneous;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Progressive;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Successful;

public interface IUpdateCopyrightView extends Progressive, Erroneous, Successful {

    void dismiss();
}
