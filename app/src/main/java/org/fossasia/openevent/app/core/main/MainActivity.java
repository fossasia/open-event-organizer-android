package org.fossasia.openevent.app.core.main;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseInjectActivity;
import org.fossasia.openevent.app.core.attendee.list.AttendeesFragment;
import org.fossasia.openevent.app.core.auth.AuthActivity;
import org.fossasia.openevent.app.core.event.about.AboutEventActivity;
import org.fossasia.openevent.app.core.event.dashboard.EventDashboardFragment;
import org.fossasia.openevent.app.core.event.list.EventListFragment;
import org.fossasia.openevent.app.core.faq.list.FaqListFragment;
import org.fossasia.openevent.app.core.organizer.detail.OrganizerDetailActivity;
import org.fossasia.openevent.app.core.settings.SettingsFragment;
import org.fossasia.openevent.app.core.ticket.list.TicketsFragment;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.User;
import org.fossasia.openevent.app.databinding.MainActivityBinding;
import org.fossasia.openevent.app.databinding.MainNavHeaderBinding;
import org.fossasia.openevent.app.ui.BackPressHandler;
import org.fossasia.openevent.app.ui.ViewUtils;

import javax.inject.Inject;

import dagger.Lazy;

public class MainActivity extends BaseInjectActivity<MainPresenter> implements NavigationView.OnNavigationItemSelectedListener, IMainView {

    public static final String EVENT_KEY = "event";
    private long eventId = -1;
    private boolean isDashboardActive = true;

    @Inject
    Lazy<MainPresenter> presenterProvider;
    @Inject
    BackPressHandler backPressHandler;

    private FragmentManager fragmentManager;
    private AlertDialog logoutDialog;

    private MainActivityBinding binding;
    private MainNavHeaderBinding headerBinding;

    private int lastSelectedNavItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity);

        headerBinding = MainNavHeaderBinding.bind(binding.navView.getHeaderView(0));

        setSupportActionBar(binding.main.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.main.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navView.setNavigationItemSelectedListener(this);

        binding.navView.getMenu().setGroupVisible(R.id.subMenu, false);
        fragmentManager = getSupportFragmentManager();

        headerBinding.profile.setOnClickListener(view -> startActivity(new Intent(this, OrganizerDetailActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPresenter().attach(this);
        getPresenter().start();
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else if (isDashboardActive) {
            backPressHandler.onBackPressed(this, super::onBackPressed);
        } else {
            getSupportFragmentManager().popBackStack();
            binding.navView.getMenu().getItem(0).setChecked(true);
            getSupportActionBar().setTitle(R.string.dashboard);
            lastSelectedNavItemId = R.id.nav_dashboard;
            isDashboardActive = true;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        binding.drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                int id = item.getItemId();

                if (id == R.id.nav_logout)
                    showLogoutDialog();
                else if (id == R.id.nav_about_event) {
                    Intent intent = new Intent(MainActivity.this, AboutEventActivity.class);
                    intent.putExtra(AboutEventActivity.EVENT_ID, eventId);
                    startActivity(intent);
                } else
                    loadFragment(id);

                binding.drawerLayout.removeDrawerListener(this);
            }
        });
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Lazy<MainPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    @Override
    public void setEventId(long eventId) {
        this.eventId = eventId;
        binding.navView.getMenu().setGroupVisible(R.id.subMenu, true);
    }

    @Override
    public void showEventList() {
        loadFragment(R.id.nav_events);
    }

    @Override
    public void showDashboard() {
        loadFragment(R.id.nav_dashboard);
    }

    @Override
    public void showOrganizer(User organizer) {
        headerBinding.setUser(organizer);
    }

    @Override
    public void invalidateDateViews() {
        headerBinding.invalidateAll();
    }

    @Override
    public void showResult(Event event) {
        headerBinding.setEvent(event);
    }

    @Override
    public void onLogout() {
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

    private void loadFragment(int navItemId) {
        if (lastSelectedNavItemId == navItemId)
            return;

        binding.navView.setCheckedItem(navItemId);
        lastSelectedNavItemId = navItemId;

        Fragment fragment;
        FragmentTransaction ft = fragmentManager.beginTransaction();

        switch (navItemId) {
            case R.id.nav_dashboard:
                fragment = EventDashboardFragment.newInstance(eventId);
                break;
            case R.id.nav_attendees:
                fragment = AttendeesFragment.newInstance(eventId);
                break;
            case R.id.nav_tickets:
                fragment = TicketsFragment.newInstance(eventId);
                break;
            case R.id.nav_events:
                fragment = EventListFragment.newInstance();
                break;
            case R.id.nav_settings:
                fragment = SettingsFragment.newInstance();
                break;
            case R.id.nav_faq:
                fragment = FaqListFragment.newInstance(eventId);
                break;
            default:
                fragment = EventDashboardFragment.newInstance(eventId);
                break;
        }

        getSupportFragmentManager().popBackStack();

        isDashboardActive = navItemId == R.id.nav_dashboard;
        if (isDashboardActive) {
            ft.replace(R.id.fragment_container, fragment);
        } else {
            ft.add(R.id.fragment_container, fragment).addToBackStack(null);
        }
        ft.commit();

    }

    private void showLogoutDialog() {
        if (logoutDialog == null)
            logoutDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.logout_confirmation)
                .setMessage(R.string.logout_confirmation_message)
                .setPositiveButton(R.string.ok, (dialog, which) -> getPresenter().logout())
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .create();

        logoutDialog.show();
    }
}
