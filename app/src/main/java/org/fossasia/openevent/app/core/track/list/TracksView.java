package org.fossasia.openevent.app.core.track.list;

import org.fossasia.openevent.app.common.mvp.view.Emptiable;
import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Refreshable;
import org.fossasia.openevent.app.data.tracks.Track;

public interface TracksView extends Progressive, Erroneous, Refreshable, Emptiable<Track> {
}
