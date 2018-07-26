package com.eventyay.organizer.core.track.list;

import com.eventyay.organizer.common.mvp.view.Emptiable;
import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Refreshable;
import com.eventyay.organizer.data.tracks.Track;

public interface TracksView extends Progressive, Erroneous, Refreshable, Emptiable<Track> {

    void openSessionsFragment(long trackId);

    void openUpdateTrackFragment(long trackId);

    void showDeleteDialog();

    void showMessage(String message);

    void changeToolbarMode(boolean toolbarEdit, boolean toolbarDelete);

    void exitContextualMenuMode();

    void enterContextualMenuMode();
}
