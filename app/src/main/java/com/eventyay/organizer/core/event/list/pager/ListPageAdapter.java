package com.eventyay.organizer.core.event.list.pager;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.common.Pipe;
import com.eventyay.organizer.data.Bus;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.databinding.EventLayoutBinding;
import com.eventyay.organizer.databinding.HeaderLayoutBinding;
import com.eventyay.organizer.ui.HeaderViewHolder;
import java.util.List;
import java.util.Locale;

class ListPageAdapter extends RecyclerView.Adapter<ListPageAdapter.EventRecyclerViewHolder>
    implements StickyRecyclerHeadersAdapter<HeaderViewHolder> {

    private List<Event> events;

    private final Bus bus;
    private boolean sortByName;

    private boolean isLongPressed;

    private final ListPageFragment fragment;

    ListPageAdapter(List<Event> events, Bus bus, ListPageFragment fragment) {
        this.events = events;
        this.bus = bus;
        this.fragment = fragment;
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis") // Inevitable DD anomaly
    public void updateList(List<Event> newEvents) {
        if (events == null) {
            events = newEvents;
            notifyItemRangeInserted(0, newEvents.size());
        } else {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return events.size();
                }

                @Override
                public int getNewListSize() {
                    return newEvents.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return events.get(oldItemPosition).getId() == newEvents.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return events.get(oldItemPosition).equals(newEvents.get(newItemPosition));
                }
            });
            events = newEvents;
            diffResult.dispatchUpdatesTo(this);
        }
    }


    @Override
    public EventRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        EventLayoutBinding binding = EventLayoutBinding.inflate(layoutInflater, parent, false);
        EventRecyclerViewHolder eventRecyclerViewHolder = new EventRecyclerViewHolder(binding);

        eventRecyclerViewHolder.onItemLongClick(fragment::openSalesSummary);
        eventRecyclerViewHolder.onItemLongClickReleased(fragment::closeSalesSummary);
        return eventRecyclerViewHolder;
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
        return events == null ? 0 : events.size();
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
        }

        public void onItemLongClick(Pipe<Long> longClickAction) {
            this.longClickAction = longClickAction;
        }

        public void onItemLongClickReleased(Runnable onLongClickReleased) {
            this.onLongClickReleased = onLongClickReleased;
        }
    }
}
