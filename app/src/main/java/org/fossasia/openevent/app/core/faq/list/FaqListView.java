package org.fossasia.openevent.app.core.faq.list;

import org.fossasia.openevent.app.common.mvp.view.Emptiable;
import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Refreshable;
import org.fossasia.openevent.app.data.faq.Faq;

public interface FaqListView extends Progressive, Erroneous, Refreshable, Emptiable<Faq> {

    void showMessage(String message);

    void exitContextualMenuMode();

    void enterContextualMenuMode();
}
