package org.fossasia.openevent.app.module.event.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import org.fossasia.openevent.app.common.app.viewholder.HeaderViewHolder;
import org.fossasia.openevent.app.common.data.contract.IBus;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.databinding.EventLayoutBinding;
import org.fossasia.openevent.app.databinding.HeaderLayoutBinding;

import java.util.List;

class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.EventRecyclerViewHolder>
    implements StickyRecyclerHeadersAdapter<HeaderViewHolder> {

    private List<Event> events;
    private IBus bus;

    EventsListAdapter(List<Event> events, IBus bus) {
        this.events = events;
        this.bus = bus;
    }

    @Override
    public EventRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        EventLayoutBinding binding = EventLayoutBinding.inflate(layoutInflater, parent, false);
        return new EventRecyclerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final EventRecyclerViewHolder holder, int position) {
        final Event thisEvent = events.get(position);
        holder.bind(thisEvent);
    }

    @Override
    public long getHeaderId(int position) {
        return events.get(position).getHeaderId();
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        return new HeaderViewHolder(HeaderLayoutBinding.inflate(
            LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder headerViewHolder, int i) {
        headerViewHolder.bindHeader(events.get(i).getHeader());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    //view holder class
    class EventRecyclerViewHolder extends RecyclerView.ViewHolder {
        private final EventLayoutBinding binding;
        private Event event;

        EventRecyclerViewHolder(EventLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding
                .getRoot()
                .setOnClickListener(view -> bus.pushSelectedEvent(event));
        }

        public void bind(Event event) {
            this.event = event;
            binding.setEvent(event);
            binding.executePendingBindings();
        }

    }

}
