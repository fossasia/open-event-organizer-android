package org.fossasia.openevent.app.data.models.delegates;

import android.view.View;

import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.core.attendee.list.viewholders.AttendeeViewHolder;

import java.util.List;

public interface IAttendeeDelegate extends Comparable<Attendee>, IHeaderProvider {
    long getIdentifier();
    int getType();
    int getLayoutRes();
    AttendeeViewHolder getViewHolder(View view);
    void bindView(AttendeeViewHolder holder, List<Object> list);
    void unbindView(AttendeeViewHolder holder);
}
