package org.fossasia.openevent.app.events;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.databinding.EventLayoutBinding;
import org.fossasia.openevent.app.event.EventContainerFragment;

import java.util.List;

class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.EventRecyclerViewHolder>{

    private List<Event> events;
    private Context context;

    private boolean isTwoPane;

    EventsListAdapter(List<Event> events, Context context, boolean isTwoPane) {
        this.events = events;
        this.context = context;
        this.isTwoPane = isTwoPane;
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

    private void showEvent(Event event) {
        EventContainerFragment fragment = EventContainerFragment.newInstance(event);
        FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
        fm.beginTransaction()
            .replace(R.id.event_detail_container, fragment)
            .commit();
    }

    /**
     * Called by the container in two pane mode to show the first event by default
     */
    void showInitialEvent() {
        if(events.isEmpty())
            return;

        showEvent(events.get(0));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    //view holder class
    class EventRecyclerViewHolder extends RecyclerView.ViewHolder{
        private final EventLayoutBinding binding;
        private final Context context;
        private final Intent intent;
        private Event event;

        EventRecyclerViewHolder(EventLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = binding.getRoot().getContext();
            this.intent = new Intent(context, EventDetailActivity.class);

            binding.getRoot().setOnClickListener(view -> {
                if (isTwoPane) {
                    showEvent(event);
                } else {
                    intent.putExtra(EventListActivity.EVENT_KEY, event);
                    context.startActivity(intent);
                }
            });
        }

        public void bind(Event event) {
            this.event = event;
            binding.setEvent(event);
            binding.executePendingBindings();
        }

    }

}
