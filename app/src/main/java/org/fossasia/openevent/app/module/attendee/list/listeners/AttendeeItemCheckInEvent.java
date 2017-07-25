package org.fossasia.openevent.app.module.attendee.list.listeners;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.utils.EventHookUtil;

import org.fossasia.openevent.app.common.data.models.Attendee;
import org.fossasia.openevent.app.module.attendee.list.contract.IAttendeesView;
import org.fossasia.openevent.app.module.attendee.list.viewholders.AttendeeViewHolder;

import java.util.List;

public class AttendeeItemCheckInEvent extends ClickEventHook<Attendee> {

    private IAttendeesView attendeesView;

    @Nullable
    @Override
    public List<View> onBindMany(@NonNull RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof AttendeeViewHolder) {
            return EventHookUtil.toList(((AttendeeViewHolder) viewHolder).getRoot());
        }
        return super.onBindMany(viewHolder);
    }

    public AttendeeItemCheckInEvent(IAttendeesView attendeesView) {
        this.attendeesView = attendeesView;
    }

    @Override
    public void onClick(View view, int position, FastAdapter<Attendee> fastAdapter, Attendee attendee) {
        attendeesView.showToggleDialog(attendee.getId());
    }
}
