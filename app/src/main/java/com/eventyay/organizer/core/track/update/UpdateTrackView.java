package com.eventyay.organizer.core.track.update;

import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Successful;
import com.eventyay.organizer.data.tracks.Track;

public interface UpdateTrackView extends Progressive, Erroneous, Successful {
    void dismiss();

    void setTrack(Track track);
}
