package org.fossasia.openevent.app.core.event.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.data.Bus;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.databinding.EventLayoutBinding;
import org.fossasia.openevent.app.databinding.HeaderLayoutBinding;
import org.fossasia.openevent.app.ui.HeaderViewHolder;

import java.util.List;
import java.util.Locale;

class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.EventRecyclerViewHolder>
    implements StickyRecyclerHeadersAdapter<HeaderViewHolder> {

    private final List<Event> events;
    private final Bus bus;

    private boolean sortByName;

    EventsListAdapter(List<Event> events, Bus bus) {
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
        if (sortByName) {
            return events.get(position).getName().substring(0, 1).toUpperCase(Locale.getDefault()).hashCode();
        } else {
            return events.get(position).getHeaderId();
        }
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        return new HeaderViewHolder(HeaderLayoutBinding.inflate(
            LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder headerViewHolder, int i) {
        if (sortByName) {
            headerViewHolder.bindHeader(events.get(i).getName().substring(0, 1).toUpperCase(Locale.getDefault()));
        } else {
            headerViewHolder.bindHeader(events.get(i).getHeader());
       }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void setSortByName(boolean sortBy) {
        sortByName = sortBy;
    }

    //view holder class
    class EventRecyclerViewHolder extends RecyclerView.ViewHolder {
        private final EventLayoutBinding binding;
        private Event event;
        private final long selectedEventId;

        EventRecyclerViewHolder(EventLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding
                .getRoot()
                .setOnClickListener(view -> bus.pushSelectedEvent(event));

            final Event selectedEvent = ContextManager.getSelectedEvent();
            selectedEventId = selectedEvent == null ? -1 : selectedEvent.getId();
        }

        public void bind(Event event) {
            this.event = event;
            binding.setEvent(event);
            binding.setSelectedEventId(selectedEventId);
            binding.executePendingBindings();
        }

    }

}
