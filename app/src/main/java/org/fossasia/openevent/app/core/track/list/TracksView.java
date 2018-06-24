package org.fossasia.openevent.app.core.track.list;

import org.fossasia.openevent.app.common.mvp.view.Emptiable;
import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Refreshable;
import org.fossasia.openevent.app.data.tracks.Track;

public interface TracksView extends Progressive, Erroneous, Refreshable, Emptiable<Track> {

    void openSessionsFragment(long trackId);

    void openUpdateTrackFragment(long trackId);

    void showDeleteDialog();

    void showMessage(String message);

    void changeToolbarMode(boolean toolbarEdit, boolean toolbarDelete);

    void exitContextualMenuMode();

    void enterContextualMenuMode();
}
