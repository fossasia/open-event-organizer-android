package org.fossasia.openevent.app.core.event.copyright.update;

import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Successful;

public interface IUpdateCopyrightView extends Progressive, Erroneous, Successful {

    void dismiss();
}
