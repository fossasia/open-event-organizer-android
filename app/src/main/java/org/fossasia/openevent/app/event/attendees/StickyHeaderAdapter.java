package org.fossasia.openevent.app.event.attendees;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.AbstractAdapter;
import com.mikepenz.fastadapter.IItem;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.databinding.AttendeeSubheaderLayoutBinding;

import java.util.List;

public class StickyHeaderAdapter extends AbstractAdapter implements StickyRecyclerHeadersAdapter {
    @Override
    public long getHeaderId(int position) {
        IItem item = getItem(position);

        if(item instanceof Attendee && ((Attendee)item).getFirstName() != null) {
            return ((Attendee) item).getFirstName().toUpperCase().charAt(0);
        }
        return -1;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        AttendeeSubheaderLayoutBinding binding;

        public ViewHolder(AttendeeSubheaderLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindHeader(String header) {
            binding.setSubheading(header);
            binding.executePendingBindings();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        return new ViewHolder(AttendeeSubheaderLayoutBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        IItem item = getItem(position);

        if(item instanceof Attendee && ((Attendee) item).getFirstName() != null) {
            ((ViewHolder) viewHolder).bindHeader(((Attendee) item).getFirstName().toUpperCase().substring(0, 1));
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }

    @Override
    public int getAdapterItemCount() {
        return 0;
    }

    @Override
    public List getAdapterItems() {
        return null;
    }

    @Override
    public IItem getAdapterItem(int i) {
        return null;
    }

    @Override
    public int getAdapterPosition(IItem item) {
        return -1;
    }

    @Override
    public int getAdapterPosition(long l) {
        return -1;
    }

    @Override
    public int getGlobalPosition(int i) {
        return -1;
    }
}
