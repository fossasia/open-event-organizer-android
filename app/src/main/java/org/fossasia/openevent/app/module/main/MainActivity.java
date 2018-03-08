package org.fossasia.openevent.app.module.main;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.app.lifecycle.view.BaseActivity;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.models.User;
import org.fossasia.openevent.app.common.utils.ui.BackPressHandler;
import org.fossasia.openevent.app.common.utils.ui.ViewUtils;
import org.fossasia.openevent.app.databinding.MainActivityBinding;
import org.fossasia.openevent.app.databinding.MainNavHeaderBinding;
import org.fossasia.openevent.app.module.attendee.list.AttendeesFragment;
import org.fossasia.openevent.app.module.auth.AuthActivity;
import org.fossasia.openevent.app.module.event.about.AboutEventActivity;
import org.fossasia.openevent.app.module.event.dashboard.EventDashboardFragment;
import org.fossasia.openevent.app.module.event.list.EventListFragment;
import org.fossasia.openevent.app.module.faq.list.FaqListFragment;
import org.fossasia.openevent.app.module.main.contract.IMainPresenter;
import org.fossasia.openevent.app.module.main.contract.IMainView;
import org.fossasia.openevent.app.module.organizer.detail.OrganizerDetailActivity;
import org.fossasia.openevent.app.module.settings.SettingsFragment;
import org.fossasia.openevent.app.module.ticket.list.TicketsFragment;

import javax.inject.Inject;

import dagger.Lazy;

public class MainActivity extends BaseActivity<IMainPresenter> implements NavigationView.OnNavigationItemSelectedListener, IMainView {

    public static final String EVENT_KEY = "event";
    private long eventId = -1;

    @Inject
    Lazy<IMainPresenter> presenterProvider;
    @Inject
    BackPressHandler backPressHandler;

    private FragmentManager fragmentManager;
    private AlertDialog logoutDialog;

    private MainActivityBinding binding;
    private MainNavHeaderBinding headerBinding;

    private int lastSelectedNavItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        OrgaApplication
            .getAppComponent()
            .inject(this);

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
        } else {
            backPressHandler.onBackPressed(this, super::onBackPressed);
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
    public Lazy<IMainPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    @Override
    public int getLoaderId() {
        return R.layout.main_activity;
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
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
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
