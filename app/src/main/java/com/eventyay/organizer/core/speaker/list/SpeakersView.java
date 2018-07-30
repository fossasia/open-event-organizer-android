package com.eventyay.organizer.core.speaker.list;

import com.eventyay.organizer.common.mvp.view.Emptiable;
import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Refreshable;
import com.eventyay.organizer.data.speaker.Speaker;

public interface SpeakersView extends Progressive, Erroneous, Refreshable, Emptiable<Speaker> {
    void openSpeakersDetailFragment(long speakerId);
}
