package org.fossasia.openevent.app.events;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import org.fossasia.openevent.app.data.contract.IBus;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.databinding.EventLayoutBinding;
import org.fossasia.openevent.app.databinding.EventSubheaderLayoutBinding;
import org.fossasia.openevent.app.events.viewholders.EventsHeaderViewHolder;
import org.fossasia.openevent.app.utils.DateService;

import java.text.ParseException;
import java.util.List;

class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.EventRecyclerViewHolder> implements StickyRecyclerHeadersAdapter<EventsHeaderViewHolder>{

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
        Event event = events.get(position);
        try {
            return DateService.getEventStatus(event).hashCode();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public EventsHeaderViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        return new EventsHeaderViewHolder(EventSubheaderLayoutBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindHeaderViewHolder(EventsHeaderViewHolder holder, int position) {
        Event event = events.get(position);
        try {
            holder.bindHeader(DateService.getEventStatus(event));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    //view holder class
    class EventRecyclerViewHolder extends RecyclerView.ViewHolder{
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
