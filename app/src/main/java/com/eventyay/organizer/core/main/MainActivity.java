package com.eventyay.organizer.core.main;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.eventyay.organizer.common.Constants;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.eventyay.organizer.R;
import com.eventyay.organizer.core.auth.AuthActivity;
import com.eventyay.organizer.core.organizer.detail.OrganizerDetailActivity;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.user.User;
import com.eventyay.organizer.databinding.MainActivityBinding;
import com.eventyay.organizer.databinding.MainNavHeaderBinding;
import com.eventyay.organizer.ui.ViewUtils;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class MainActivity extends AppCompatActivity implements
    NavigationView.OnNavigationItemSelectedListener, MainView, HasSupportFragmentInjector {

    public static final String EVENT_KEY = "event";
    private long eventId = -1;

    private final List<Integer> drawerItems = Arrays.asList(
        R.id.nav_dashboard,
        R.id.nav_attendees,
        R.id.nav_share,
        //R.id.nav_about_event,
        R.id.nav_event_settings);

    private final List<Integer> drawerExtraItems = Arrays.asList(
        R.id.nav_sell,
        R.id.nav_orders,
        R.id.nav_tickets,
        R.id.nav_edit_event,
        R.id.nav_feedback,
        R.id.nav_faq,
        R.id.nav_track,
        R.id.nav_sponsor,
        R.id.nav_speaker,
        R.id.nav_speakers_call,
        R.id.nav_roles);

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    private FragmentNavigator fragmentNavigator;
    private DrawerNavigator drawerNavigator;

    private MainActivityBinding binding;
    private MainNavHeaderBinding headerBinding;
    private OrganizerViewModel organizerViewModel;
    private EventViewModel eventViewModel;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity);

        headerBinding = MainNavHeaderBinding.bind(binding.navView.getHeaderView(0));

        organizerViewModel = ViewModelProviders.of(this, viewModelFactory).get(OrganizerViewModel.class);
        eventViewModel = ViewModelProviders.of(this, viewModelFactory).get(EventViewModel.class);

        setSupportActionBar(binding.main.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.main.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navView.setNavigationItemSelectedListener(this);

        binding.navView.getMenu().setGroupVisible(R.id.subMenu, false);
        fragmentNavigator = new FragmentNavigator(getSupportFragmentManager(), eventId);
        drawerNavigator = new DrawerNavigator(this, fragmentNavigator, organizerViewModel);

        headerBinding.profile.setOnClickListener(view -> startActivity(new Intent(this, OrganizerDetailActivity.class)));

        sharedPreferences = getSharedPreferences(Constants.FOSS_PREFS, MODE_PRIVATE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        organizerViewModel.setLocalDatePreferenceAction();
        organizerViewModel.getOrganizer().observe(this, this::showOrganizer);
        organizerViewModel.getLogoutAction().observe(this, aVoid -> this.onLogout());
        organizerViewModel.getLocalDatePreferenceAction().observe(this, aVoid -> this.invalidateDateViews());
        organizerViewModel.getError().observe(this, this::showError);
        eventViewModel.onStart();
        eventViewModel.getEventId().observe(this, this::setEventId);
        eventViewModel.getSelectedEvent().observe(this, this::showResult);
        eventViewModel.getError().observe(this, this::showError);
        eventViewModel.getShowDashboard().observe(this, aVoid -> this.showDashboard());
        eventViewModel.getShowEventList().observe(this, aVoid -> this.showEventList());
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else if (fragmentNavigator.isMyEventsActive())
            super.onBackPressed();
        else if (eventId == -1)
            finish();
        else {
            int lastSelectedNavItemId = fragmentNavigator.back();

            if (lastSelectedNavItemId == R.id.nav_events)
                unselectEvent();

            binding.navView.getMenu().findItem(lastSelectedNavItemId).setChecked(true);
            getSupportActionBar().setTitle(lastSelectedNavItemId);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.nav_events)
            unselectEvent();

        binding.drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                binding.navView.setCheckedItem(item.getItemId());
                drawerNavigator.selectItem(item);
                binding.drawerLayout.removeDrawerListener(this);
            }
        });
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void setEventId(long eventId) {
        this.eventId = eventId;
        fragmentNavigator.setEventId(eventId);

        for (Integer itemId : drawerItems) {
            binding.navView.getMenu().findItem(itemId).setVisible(true);
        }

        boolean isDeveloperModeEnabled = sharedPreferences.getBoolean(
            getString(R.string.developer_mode_key), false);

        if (isDeveloperModeEnabled) {
            for (Integer itemId : drawerExtraItems) {
                binding.navView.getMenu().findItem(itemId).setVisible(true);
            }
        }
    }

    @Override
    public void unselectEvent() {
        fragmentNavigator.setEventId(-1);
        eventViewModel.unselectEvent();

        for (Integer itemId : drawerItems) {
            binding.navView.getMenu().findItem(itemId).setVisible(false);
        }

        boolean isDeveloperModeEnabled = sharedPreferences.getBoolean(
            getString(R.string.developer_mode_key), false);

        if (isDeveloperModeEnabled) {
            for (Integer itemId : drawerExtraItems) {
                binding.navView.getMenu().findItem(itemId).setVisible(false);
            }
        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
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
        binding.navView.setCheckedItem(navItemId);

        fragmentNavigator.loadFragment(navItemId);
    }
}
