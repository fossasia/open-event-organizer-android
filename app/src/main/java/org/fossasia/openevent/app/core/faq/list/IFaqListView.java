package org.fossasia.openevent.app.core.faq.list;

import org.fossasia.openevent.app.common.mvp.view.Emptiable;
import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Refreshable;
import org.fossasia.openevent.app.data.models.Faq;

public interface IFaqListView extends Progressive, Erroneous, Refreshable, Emptiable<Faq> {

}
