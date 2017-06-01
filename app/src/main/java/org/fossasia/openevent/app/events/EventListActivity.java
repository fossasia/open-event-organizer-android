package org.fossasia.openevent.app.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.data.EventDataRepository;
import org.fossasia.openevent.app.data.LoginModel;
import org.fossasia.openevent.app.data.UtilModel;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.events.contract.IEventsPresenter;
import org.fossasia.openevent.app.events.contract.IEventsView;
import org.fossasia.openevent.app.login.LoginActivity;
import org.fossasia.openevent.app.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * An activity representing a list of Events. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link EventDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class EventListActivity extends AppCompatActivity implements IEventsView {

    @BindView(R.id.event_list)
    RecyclerView recyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean isTwoPane;

    private List<Event> events;
    private EventsListAdapter eventListAdapter;

    public static final String EVENT_KEY = "event";

    private IEventsPresenter presenter;

    private IUtilModel utilModel;

    // Lifecycle methods start

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.event_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            isTwoPane = true;
        }

        events = new ArrayList<>();
        eventListAdapter = new EventsListAdapter(events, this, isTwoPane);

        assert recyclerView != null;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(eventListAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this , DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        utilModel = new UtilModel(this);
        presenter = new EventsPresenter(this, new EventDataRepository(utilModel), new LoginModel(utilModel));
        presenter.attach();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        ViewUtils.showView(progressBar, show);
    }

    @Override
    public void showEvents(List<Event> events) {
        this.events.clear();
        this.events.addAll(events);
        eventListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showOrganiserName(String name) {
        toolbar.setSubtitle(utilModel.getResourceString(R.string.subtitle_organizer) + name);
    }

    @Override
    public void showEventError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showOrganiserLoadError(String error) {
        Timber.d(error);
    }

    @Override
    public void onLogout() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    // View Implementation end

}
