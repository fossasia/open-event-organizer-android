package org.fossasia.openevent.app.core.sponsor.list;

import org.fossasia.openevent.app.common.mvp.view.Emptiable;
import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Refreshable;
import org.fossasia.openevent.app.data.sponsor.Sponsor;

public interface SponsorsView extends Progressive, Erroneous, Refreshable, Emptiable<Sponsor> {

    void openUpdateSponsorFragment(long sponsorId);

    void showAlertDialog(long sponsorId);

    void showSponsorDeleted(String message);

}
