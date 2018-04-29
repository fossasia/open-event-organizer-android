package org.fossasia.openevent.app.core.track.update;

import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Successful;
import org.fossasia.openevent.app.data.tracks.Track;

public interface UpdateTrackView extends Progressive, Erroneous, Successful {
    void dismiss();

    void setTrack(Track track);
}
