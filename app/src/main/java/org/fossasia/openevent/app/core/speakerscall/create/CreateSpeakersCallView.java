package org.fossasia.openevent.app.core.speakerscall.create;

import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Successful;

public interface CreateSpeakersCallView extends Progressive, Successful, Erroneous {

    void dismiss();
}
