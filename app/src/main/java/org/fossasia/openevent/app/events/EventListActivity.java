package org.fossasia.openevent.app.events;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.databinding.library.baseAdapters.BR;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.databinding.ActivityEventListBinding;
import org.fossasia.openevent.app.events.contract.IEventsPresenter;
import org.fossasia.openevent.app.events.contract.IEventsView;
import org.fossasia.openevent.app.login.LoginActivity;
import org.fossasia.openevent.app.utils.Utils;
import org.fossasia.openevent.app.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean isTwoPane;

    private final List<Event> events = new ArrayList<>();
    private EventsListAdapter eventListAdapter;

    public static final String EVENT_KEY = "event";
    public static final String EVENT_NAME = "event_name";

    @Inject
    IUtilModel utilModel;

    @Inject
    IEventsPresenter presenter;

    private ActivityEventListBinding binding;

    // Lifecycle methods start

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        OrgaApplication
            .getAppComponent(this)
            .inject(this);

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_list);

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setTitle(getTitle());

        isTwoPane = binding.eventListContainer.eventDetailContainer != null;

        binding.setEvents(events);
        eventListAdapter = new EventsListAdapter(events, this, isTwoPane);

        RecyclerView recyclerView = binding.eventListContainer.eventList.eventRecyclerview;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(eventListAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this , DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        presenter.attach(this);
        presenter.start();
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
    public boolean isTwoPane() {
        return isTwoPane;
    }

    @Override
    public void showProgressBar(boolean show) {
        ViewUtils.showView(binding.eventListContainer.eventList.progressBar, show);
    }

    @Override
    public void showEvents(List<Event> events) {
        this.events.clear();
        this.events.addAll(events);
        eventListAdapter.notifyDataSetChanged();
        binding.setVariable(BR.events, events);
        binding.executePendingBindings();
    }

    @Override
    public void showInitialEvent() {
        eventListAdapter.showInitialEvent();
    }

    @Override
    public void showOrganiserName(String name) {
        binding.toolbar.setSubtitle(Utils.formatOptionalString(
            utilModel.getResourceString(R.string.subtitle_organizer), name)
        );
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
