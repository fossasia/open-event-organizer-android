package com.eventyay.organizer.core.track.create;

import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Successful;
import com.eventyay.organizer.data.tracks.Track;

public interface CreateTrackView extends Progressive, Erroneous, Successful {

    void dismiss();

    void setTrack(Track track);
}
