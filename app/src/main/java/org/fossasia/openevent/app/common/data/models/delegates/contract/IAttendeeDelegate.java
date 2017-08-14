package org.fossasia.openevent.app.common.data.models.delegates.contract;

import android.view.View;

import org.fossasia.openevent.app.common.data.models.Attendee;
import org.fossasia.openevent.app.common.data.models.contract.IHeaderProvider;
import org.fossasia.openevent.app.module.attendee.list.viewholders.AttendeeViewHolder;

import java.util.List;

public interface IAttendeeDelegate extends Comparable<Attendee>, IHeaderProvider {
    long getIdentifier();
    int getType();
    int getLayoutRes();
    AttendeeViewHolder getViewHolder(View view);
    void bindView(AttendeeViewHolder holder, List<Object> list);
    void unbindView(AttendeeViewHolder holder);
}
