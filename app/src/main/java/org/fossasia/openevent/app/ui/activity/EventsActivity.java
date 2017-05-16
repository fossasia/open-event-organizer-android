package org.fossasia.openevent.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.contract.presenter.EventListPresenter;
import org.fossasia.openevent.app.contract.view.EventListView;
import org.fossasia.openevent.app.data.AndroidUtilModel;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.network.api.RetrofitEventModel;
import org.fossasia.openevent.app.ui.adapters.EventListAdapter;
import org.fossasia.openevent.app.ui.presenter.EventsActivityPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventsActivity extends AppCompatActivity implements EventListView {

    @BindView(R.id.rvEventList)
    RecyclerView recyclerView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    public static List<Event> events = new ArrayList<>();

    private EventListAdapter eventListAdapter = new EventListAdapter(events, this);
    private EventListPresenter presenter;

    // Lifecycle methods start

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        ButterKnife.bind(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(eventListAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this , DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        AndroidUtilModel utilModel = new AndroidUtilModel(this);
        presenter = new EventsActivityPresenter(this,
            new RetrofitEventModel(utilModel),
            utilModel);

        presenter.attach();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Detach view from presenter
        presenter.detach();
    }

    // Lifecycle methods end

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_events, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                presenter.logout();
                return true;
            default:
                return true;
        }
    }

    // View Implementation start

    @Override
    public void showProgressBar(boolean show) {
        int mode = View.GONE;

        if(show)
            mode = View.VISIBLE;

        progressBar.setVisibility(mode);
    }

    @Override
    public void showEvents(List<Event> eventList) {
        events.clear();
        events.addAll(eventList);
        eventListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEventError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLogout() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    // View Implementation end
}
