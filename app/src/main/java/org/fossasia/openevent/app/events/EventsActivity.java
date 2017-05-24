package org.fossasia.openevent.app.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.data.EventDataRepository;
import org.fossasia.openevent.app.data.UtilModel;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.events.contract.IEventsPresenter;
import org.fossasia.openevent.app.events.contract.IEventsView;
import org.fossasia.openevent.app.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.fossasia.openevent.app.utils.AndroidUtils.showView;

public class EventsActivity extends AppCompatActivity implements IEventsView {

    @BindView(R.id.tvOrganiserName)
    TextView organiserName;

    @BindView(R.id.rvEventList)
    RecyclerView recyclerView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    public static List<Event> events = new ArrayList<>();

    private EventsListAdapter eventListAdapter = new EventsListAdapter(events, this);
    private IEventsPresenter presenter;

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

        UtilModel utilModel = new UtilModel(this);
        presenter = new EventsPresenter(this, new EventDataRepository(utilModel), utilModel);

        presenter.attach();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Detach view from presenter
        presenter.detach();
    }

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

    // Lifecycle methods end

    // View Implementation start

    @Override
    public void showProgressBar(boolean show) {
        showView(progressBar, show);
    }

    @Override
    public void showOrganiserPanel(boolean show) {
        showView(organiserName, show);
    }

    @Override
    public void showEvents(List<Event> eventList) {
        events.clear();
        events.addAll(eventList);
        eventListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showOrganiserName(String name) {
        organiserName.setText(name);
    }

    @Override
    public void showEventError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showOrganiserLoadError(String error) {
        Log.d("TAG", error);
    }

    @Override
    public void onLogout() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    // View Implementation end
}
