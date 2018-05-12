package org.fossasia.openevent.app.core.speaker.list;

import org.fossasia.openevent.app.common.mvp.view.Emptiable;
import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Refreshable;
import org.fossasia.openevent.app.data.speaker.Speaker;

public interface SpeakersView extends Progressive, Erroneous, Refreshable, Emptiable<Speaker> {
}
