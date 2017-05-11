package org.fossasia.openevent.app.Views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.fossasia.openevent.app.Adapters.EventListAdapter;
import org.fossasia.openevent.app.Api.ApiCall;
import org.fossasia.openevent.app.Interfaces.VolleyCallBack;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.Utils.Constants;
import org.fossasia.openevent.app.Utils.Network;
import org.fossasia.openevent.app.model.UserEvents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventsActivity extends AppCompatActivity {

    public static final String TAG = "EventsActivity";
    RecyclerView recyclerView;
    static ArrayList<UserEvents> userEventsArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        recyclerView = (RecyclerView) findViewById(R.id.rvEventList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        final EventListAdapter eventListAdapter = new EventListAdapter(userEventsArrayList , this);
        recyclerView.setAdapter(eventListAdapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this , DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);


        VolleyCallBack volleyCallBack = new VolleyCallBack() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                UserEvents[] userEvents = gson.fromJson(response , UserEvents[].class);
                for (UserEvents event : userEvents){
                    Log.d(TAG, "onSuccess: " + event.getName());
                }
                List<UserEvents> eventList = Arrays.asList(userEvents);
                userEventsArrayList.addAll(eventList);
                eventListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(VolleyError error) {
                Log.d(TAG, "onError: " + error.toString());
                Toast.makeText(EventsActivity.this, "Could not fetch Events", Toast.LENGTH_SHORT).show();

            }
        };
        if(Network.isNetworkConnected(this)) {
            ApiCall.callApi(this, Constants.userEvents, volleyCallBack);
        }else{
            Toast.makeText(this, Constants.noNetwork, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
