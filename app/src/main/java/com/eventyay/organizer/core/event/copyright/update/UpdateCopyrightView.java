package com.eventyay.organizer.core.event.copyright.update;

import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Successful;
import com.eventyay.organizer.data.copyright.Copyright;

public interface UpdateCopyrightView extends Progressive, Erroneous, Successful {

    void dismiss();

    void setCopyright(Copyright copyright);
}
