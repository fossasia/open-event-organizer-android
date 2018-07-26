package com.eventyay.organizer.core.sponsor.create;

import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Successful;
import com.eventyay.organizer.data.sponsor.Sponsor;

public interface CreateSponsorView extends Progressive, Erroneous, Successful {

    void dismiss();

    void setSponsor(Sponsor sponsor);
}
