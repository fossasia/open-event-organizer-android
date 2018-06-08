package org.fossasia.openevent.app.core.speakerscall.detail;

import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.ItemResult;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Refreshable;
import org.fossasia.openevent.app.data.speakerscall.SpeakersCall;

public interface SpeakersCallView extends Progressive, Erroneous, Refreshable, ItemResult<SpeakersCall> {
}
