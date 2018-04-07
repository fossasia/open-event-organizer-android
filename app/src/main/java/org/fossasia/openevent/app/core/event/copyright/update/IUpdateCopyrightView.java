package org.fossasia.openevent.app.core.event.copyright.update;

import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Successful;
import org.fossasia.openevent.app.data.copyright.Copyright;

public interface IUpdateCopyrightView extends Progressive, Erroneous, Successful {

    void dismiss();

    void setCopyright(Copyright copyright);
}
