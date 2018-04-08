package org.fossasia.openevent.app.core.feedback.list;


import org.fossasia.openevent.app.common.mvp.view.Emptiable;
import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Refreshable;
import org.fossasia.openevent.app.data.feedback.Feedback;

public interface FeedbackListView extends Progressive, Erroneous, Refreshable, Emptiable<Feedback> {

}
