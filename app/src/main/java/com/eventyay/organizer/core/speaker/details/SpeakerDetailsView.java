package com.eventyay.organizer.core.speaker.details;

import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.ItemResult;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Refreshable;
import com.eventyay.organizer.data.session.Session;
import com.eventyay.organizer.data.speaker.Speaker;

import java.util.List;

public interface SpeakerDetailsView extends Progressive, Erroneous, Refreshable, ItemResult<Speaker> {

    void showSessions(List<Session> sessions);
}
