package org.fossasia.openevent.app.core.sponsor.create;

import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Successful;
import org.fossasia.openevent.app.data.sponsor.Sponsor;

public interface CreateSponsorView extends Progressive, Erroneous, Successful {

    void dismiss();

    void setSponsor(Sponsor sponsor);
}
