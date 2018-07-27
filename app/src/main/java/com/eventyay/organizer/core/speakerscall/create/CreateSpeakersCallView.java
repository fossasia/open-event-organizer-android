package com.eventyay.organizer.core.speakerscall.create;

import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Successful;

public interface CreateSpeakersCallView extends Progressive, Successful, Erroneous {

    void dismiss();
}
