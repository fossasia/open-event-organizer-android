package com.eventyay.organizer.data.attendee;

import android.view.View;

import com.eventyay.organizer.core.attendee.list.viewholders.AttendeeViewHolder;
import com.eventyay.organizer.common.model.HeaderProvider;

import java.util.List;

public interface AttendeeDelegate extends Comparable<Attendee>, HeaderProvider {
    long getIdentifier();
    int getType();
    int getLayoutRes();
    AttendeeViewHolder getViewHolder(View view);
    void bindView(AttendeeViewHolder holder, List<Object> list);
    void unbindView(AttendeeViewHolder holder);
}
