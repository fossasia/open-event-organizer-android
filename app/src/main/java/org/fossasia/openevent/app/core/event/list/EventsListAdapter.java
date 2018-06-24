package org.fossasia.openevent.app.core.event.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import org.fossasia.openevent.app.common.ContextManager;
import org.fossasia.openevent.app.common.Pipe;
import org.fossasia.openevent.app.data.Bus;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.databinding.EventLayoutBinding;
import org.fossasia.openevent.app.databinding.HeaderLayoutBinding;
import org.fossasia.openevent.app.ui.HeaderViewHolder;
import org.fossasia.openevent.app.utils.service.DateService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.EventRecyclerViewHolder>
    implements StickyRecyclerHeadersAdapter<HeaderViewHolder>, Filterable {

    private final List<Event> events;
    private final List<Event> selectedEvents = new ArrayList<>();

    private final Bus bus;
    private final EventsPresenter eventsPresenter;
    private boolean sortByName;

    private boolean isLongPressed;

    EventsListAdapter(List<Event> events, Bus bus, EventsPresenter eventsPresenter) {
        this.events = events;
        this.bus = bus;
        this.eventsPresenter = eventsPresenter;
    }

    public void categorizeEvents() {

        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                selectedEvents.clear();
                for (Event event : events) {
                    try {
                        String category = DateService.getEventStatus(event);
                        if (constraint.toString().equalsIgnoreCase(category))
                            selectedEvents.add(event);
                    } catch (ParseException e) {
                        Timber.e(e);
                    }
                }
                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public EventRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        EventLayoutBinding binding = EventLayoutBinding.inflate(layoutInflater, parent, false);
        EventRecyclerViewHolder eventRecyclerViewHolder = new EventRecyclerViewHolder(binding);

        eventRecyclerViewHolder.onItemLongClick(eventsPresenter::openSalesSummary);
        eventRecyclerViewHolder.onItemLongClickReleased(eventsPresenter::closeSalesSummary);
        return eventRecyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(final EventRecyclerViewHolder holder, int position) {
        final Event thisEvent = selectedEvents.get(position);
        holder.bind(thisEvent);
    }

    @Override
    public long getHeaderId(int position) {
        if (sortByName) {
            return selectedEvents.get(position).getName().substring(0, 1).toUpperCase(Locale.getDefault()).hashCode();
        } else {
            return selectedEvents.get(position).getHeaderId();
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
            headerViewHolder.bindHeader(selectedEvents.get(i).getName().substring(0, 1).toUpperCase(Locale.getDefault()));
        } else {
            headerViewHolder.bindHeader(selectedEvents.get(i).getHeader());
       }
    }

    @Override
    public int getItemCount() {
        return selectedEvents.size();
    }

    public void setSortByName(boolean sortBy) {
        sortByName = sortBy;
    }

    //view holder class
    class EventRecyclerViewHolder extends RecyclerView.ViewHolder {
        private final EventLayoutBinding binding;
        private Event event;
        private final long selectedEventId;
        private Pipe<Long> longClickAction;
        private Runnable onClick;
        private Runnable onLongClickReleased;

        EventRecyclerViewHolder(EventLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding
                .getRoot()
                .setOnClickListener(view -> {
                    bus.pushSelectedEvent(event);
                });

            binding
                .getRoot()
                .setOnLongClickListener(view -> {
                    if (longClickAction != null && !isLongPressed) {
                        longClickAction.push(event.id);
                        isLongPressed = true;
                    }
                    return true;
                });

            binding
                .getRoot()
                .setOnTouchListener((view, motionEvent) -> {
                    view.onTouchEvent(motionEvent);
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP && isLongPressed) {
                        onLongClickReleased.run();
                        isLongPressed = false;
                    }
                    return false;
                });

            final Event selectedEvent = ContextManager.getSelectedEvent();
            selectedEventId = selectedEvent == null ? -1 : selectedEvent.getId();
        }

        public void bind(Event event) {
            this.event = event;
            binding.setEvent(event);
            binding.setSelectedEventId(selectedEventId);
            binding.executePendingBindings();
            binding.setEventsPresenter(eventsPresenter);
        }

        public void onItemLongClick(Pipe<Long> longClickAction) {
            this.longClickAction = longClickAction;
        }

        public void onItemClick(Runnable onClick) {
            this.onClick = onClick;
        }

        public void onItemLongClickReleased(Runnable onLongClickReleased) {
            this.onLongClickReleased = onLongClickReleased;
        }
    }
}
