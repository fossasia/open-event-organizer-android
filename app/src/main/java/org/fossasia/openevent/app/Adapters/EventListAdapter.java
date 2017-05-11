package org.fossasia.openevent.app.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.Views.EventDetailsActivity;
import org.fossasia.openevent.app.model.UserEvents;

import java.util.ArrayList;


public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventRecyclerViewHolder>{

    static ArrayList<UserEvents> eventsArrayList = new ArrayList<>();
    Activity activity;

    public EventListAdapter(ArrayList<UserEvents> eventsArrayList ,Activity context) {
        this.eventsArrayList = eventsArrayList;
        this.activity = context;
    }

    @Override
    public EventRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater li = activity.getLayoutInflater();
        View itemView = li.inflate(R.layout.event_layout,null);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        EventRecyclerViewHolder eventRecyclerViewHolder  = new EventRecyclerViewHolder(itemView);
        return eventRecyclerViewHolder;
    }



    @Override
    public void onBindViewHolder(final EventRecyclerViewHolder holder, int position) {

        final UserEvents thisEvent = eventsArrayList.get(position);
        holder.eventTitle.setText(thisEvent.getName());
        Picasso.with(activity).load(thisEvent.getThumbnail()).into(holder.eventImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity , EventDetailsActivity.class);
                i.putExtra("position", holder.getAdapterPosition());
                i.putExtra("id",thisEvent.getId());
                activity.startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return eventsArrayList.size();
    }


    //view holder class
    public  class EventRecyclerViewHolder extends RecyclerView.ViewHolder{
        TextView eventTitle;
        ImageView eventImage;

        public EventRecyclerViewHolder(View itemView) {
            super(itemView);
            eventTitle = (TextView) itemView.findViewById(R.id.tvEventTitle);
            eventImage = (ImageView) itemView.findViewById(R.id.ivEventProfile);
        }

    }

}
