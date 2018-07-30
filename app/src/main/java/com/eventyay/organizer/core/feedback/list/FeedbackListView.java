package com.eventyay.organizer.core.feedback.list;


import com.eventyay.organizer.common.mvp.view.Emptiable;
import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Refreshable;
import com.eventyay.organizer.data.feedback.Feedback;

public interface FeedbackListView extends Progressive, Erroneous, Refreshable, Emptiable<Feedback> {

}
