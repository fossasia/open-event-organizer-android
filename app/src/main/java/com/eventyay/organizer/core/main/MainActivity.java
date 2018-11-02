package com.eventyay.organizer.core.main;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.eventyay.organizer.BuildConfig;
import com.eventyay.organizer.R;
import com.eventyay.organizer.core.auth.AuthActivity;
import com.eventyay.organizer.core.organizer.detail.OrganizerDetailActivity;
import com.eventyay.organizer.core.settings.SettingsFragment;
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
        R.id.nav_feedback,
        R.id.nav_faq,
        R.id.nav_track,
        R.id.nav_sponsor,
        R.id.nav_speaker,
        R.id.nav_speakers_call,
        R.id.nav_about_event);

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
        } else if (fragmentNavigator.isDashboardActive())
            super.onBackPressed();
        else if (eventId == -1) {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if(f instanceof SettingsFragment)
            {
                finish();
                ((SettingsFragment) f).backToMainActivity();
            }
            else {
                finish();
            }
        }
        else{
            fragmentNavigator.back();
            binding.navView.getMenu().findItem(R.id.nav_dashboard).setChecked(true);
            getSupportActionBar().setTitle(R.string.dashboard);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
        binding.navView.getMenu().setGroupVisible(R.id.subMenu, true);

        if (BuildConfig.HIDE_DRAWER_ITEMS) {
            for (Integer itemId : drawerItems) {
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
