package com.eventyay.organizer.core.sponsor.list;

import com.eventyay.organizer.common.mvp.view.Emptiable;
import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Refreshable;
import com.eventyay.organizer.data.sponsor.Sponsor;

public interface SponsorsView extends Progressive, Erroneous, Refreshable, Emptiable<Sponsor> {

    void openUpdateSponsorFragment(long sponsorId);

    void showMessage(String message);

    void changeToolbarMode(boolean toolbarEdit, boolean toolbarDelete);

    void exitContextualMenuMode();

    void enterContextualMenuMode();
}
