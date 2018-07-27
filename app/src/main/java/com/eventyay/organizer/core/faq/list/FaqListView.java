package com.eventyay.organizer.core.faq.list;

import com.eventyay.organizer.common.mvp.view.Emptiable;
import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Refreshable;
import com.eventyay.organizer.data.faq.Faq;

public interface FaqListView extends Progressive, Erroneous, Refreshable, Emptiable<Faq> {

    void showMessage(String message);

    void exitContextualMenuMode();

    void enterContextualMenuMode();
}
