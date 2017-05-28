package org.fossasia.openevent.app.events;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.event.EventContainerActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.EventRecyclerViewHolder>{

    private List<Event> events;
    private Context context;

    EventsListAdapter(List<Event> events, Context context) {
        this.events = events;
        this.context = context;
    }

    @Override
    public EventRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_layout, parent, false);

        return new EventRecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final EventRecyclerViewHolder holder, int position) {

        final Event thisEvent = events.get(position);
        holder.eventTitle.setText(thisEvent.getName());

        String thumbnail = thisEvent.getThumbnail();

        if(thumbnail != null && !TextUtils.isEmpty(thumbnail)) {
            Picasso.with(context)
                .load(thumbnail)
                .into(holder.eventImage);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventContainerActivity.class);
            intent.putExtra(EventsActivity.EVENT_KEY, thisEvent);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    //view holder class
    class EventRecyclerViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvEventTitle)
        TextView eventTitle;
        @BindView(R.id.ivEventProfile)
        ImageView eventImage;

        EventRecyclerViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

    }

}
