package org.fossasia.openevent.app.core.faq.create;

import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Successful;

public interface ICreateFaqView extends Progressive, Erroneous, Successful {

    void dismiss();

}
