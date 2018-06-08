package org.fossasia.openevent.app.core.speaker.details;

import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.ItemResult;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Refreshable;
import org.fossasia.openevent.app.data.session.Session;
import org.fossasia.openevent.app.data.speaker.Speaker;

import java.util.List;

public interface SpeakerDetailsView extends Progressive, Erroneous, Refreshable, ItemResult<Speaker> {

    void showSessions(List<Session> sessions);
}
