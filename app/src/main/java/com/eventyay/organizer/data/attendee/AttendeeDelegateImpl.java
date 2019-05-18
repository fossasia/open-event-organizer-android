package com.eventyay.organizer.data.attendee;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import android.view.View;

import com.mikepenz.fastadapter.items.AbstractItem;

import com.eventyay.organizer.R;
import com.eventyay.organizer.core.attendee.list.viewholders.AttendeeViewHolder;
import com.eventyay.organizer.common.model.HeaderProvider;
import com.eventyay.organizer.utils.CompareUtils;

import java.util.List;

public class AttendeeDelegateImpl extends AbstractItem<Attendee, AttendeeViewHolder> implements Comparable<Attendee>, HeaderProvider {

    private final Attendee attendee;

    public AttendeeDelegateImpl(Attendee attendee) {
        this.attendee = attendee;
    }

    @Override
    public int compareTo(@NonNull Attendee other) {
        return CompareUtils.compareCascading(attendee, other,
            Attendee::getFirstname, Attendee::getLastname, Attendee::getEmail
        );
    }

    @Override
    public long getIdentifier() {
        return attendee.getId();
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.attendee_layout;
    }

    @Override
    public AttendeeViewHolder getViewHolder(@NonNull View view) {
        return new AttendeeViewHolder(DataBindingUtil.bind(view));
    }

    @Override
    public void bindView(@NonNull AttendeeViewHolder holder, @NonNull List<Object> list) {
        super.bindView(holder, list);
        holder.bindAttendee(attendee);
    }

    @Override
    public void unbindView(@NonNull AttendeeViewHolder holder) {
        super.unbindView(holder);
        holder.unbindAttendee();
    }

    @Override
    public String getHeader() {
        return attendee.getFirstname().substring(0, 1);
    }

    @Override
    public long getHeaderId() {
        return getHeader().charAt(0);
    }

}
