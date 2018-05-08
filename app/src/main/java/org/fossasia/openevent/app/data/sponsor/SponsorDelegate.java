package org.fossasia.openevent.app.data.sponsor;

import android.view.View;

import org.fossasia.openevent.app.common.model.HeaderProvider;
import org.fossasia.openevent.app.core.sponsor.list.viewholder.SponsorsViewHolder;

public interface SponsorDelegate extends Comparable<Sponsor>, HeaderProvider {

    int getType();
    int getLayoutRes();
    SponsorsViewHolder getViewHolder(View view);

}
