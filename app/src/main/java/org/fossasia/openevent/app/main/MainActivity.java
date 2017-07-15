package org.fossasia.openevent.app.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.event.attendees.AttendeesFragment;
import org.fossasia.openevent.app.event.detail.EventDetailFragment;
import org.fossasia.openevent.app.events.EventListFragment;
import org.fossasia.openevent.app.login.LoginActivity;
import org.fossasia.openevent.app.main.contract.IMainPresenter;
import org.fossasia.openevent.app.main.contract.IMainView;
import org.fossasia.openevent.app.utils.DateUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IMainView {

    public static final String EVENT_KEY = "event";
    private static final int BACK_PRESS_RESET_TIME = 2000;
    private long backPressed;
    private long eventId = -1;

    @Inject
    IMainPresenter presenter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private TextView tvEventName;
    private TextView tvEventTime;

    private FragmentManager fragmentManager;
    private AlertDialog logoutDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        OrgaApplication
            .getAppComponent(this)
            .inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        View navHeader = navigationView.getHeaderView(0);
        tvEventName = (TextView) navHeader.findViewById(R.id.tvEventName);
        tvEventTime = (TextView) navHeader.findViewById(R.id.tvEventTime);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().setGroupVisible(R.id.subMenu, false);
        fragmentManager = getSupportFragmentManager();
        loadFragment(R.id.nav_events);

        presenter.attach(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detach();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (backPressed + BACK_PRESS_RESET_TIME > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_logout)
            showLogoutDialog();
        else if (eventId != -1)
            loadFragment(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void loadDashboard(long eventId) {
        navigationView.getMenu().setGroupVisible(R.id.subMenu, true);
        this.eventId = eventId;
        loadFragment(R.id.nav_dashboard);
    }

    @Override
    public void showResult(Event event) {
        setDrawerHeader(event.getName(),
            DateUtils.formatDateWithDefault(
                DateUtils.FORMAT_DATE_COMPLETE,
                event.getStartTime()
            )
        );
    }

    @Override
    public void onLogout() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    private void loadFragment(int navItemId) {
        navigationView.setCheckedItem(navItemId);

        Fragment fragment;
        switch (navItemId) {
            case R.id.nav_dashboard:
                fragment = EventDetailFragment.newInstance(eventId);
                break;
            case R.id.nav_attendees:
                fragment = AttendeesFragment.newInstance(eventId);
                break;
            case R.id.nav_events:
                fragment = EventListFragment.newInstance();
                break;
            default:
                fragment = EventDetailFragment.newInstance(eventId);
        }
        setTitle(navigationView.getMenu().findItem(navItemId).getTitle());
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
    }

    private void setDrawerHeader(String eventName, String eventTime) {
        tvEventName.setText(eventName);
        tvEventTime.setText(eventTime);
    }

    private void showLogoutDialog() {
        if (logoutDialog == null)
            logoutDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.logout_confirmation)
                .setMessage(R.string.logout_confirmation_message)
                .setPositiveButton(R.string.ok, (dialog, which) -> presenter.logout())
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .create();

        logoutDialog.show();
    }
}
