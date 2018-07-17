package org.fossasia.openevent.app.core.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.core.main.MainActivity;
import org.fossasia.openevent.app.core.settings.restriction.CheckInRestrictions;
import org.fossasia.openevent.app.ui.ViewUtils;

public class EventSettingsFragment extends PreferenceFragmentCompat {

    private long eventId;

    public static EventSettingsFragment newInstance(long eventId) {
        EventSettingsFragment fragment = new EventSettingsFragment();
        Bundle args = new Bundle();
        args.putLong(MainActivity.EVENT_KEY, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            eventId = getArguments().getLong(MainActivity.EVENT_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(R.color.color_top_surface));
        return view;
    }

    @Override
    public void onCreatePreferencesFix(@Nullable Bundle bundle, String rootKey) {
        PreferenceManager manager = getPreferenceManager();
        manager.setSharedPreferencesName(Constants.FOSS_PREFS);

        setPreferencesFromResource(R.xml.event_preferences, rootKey);

        findPreference(getString(R.string.payment_preference_key)).setOnPreferenceClickListener(preference -> {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction
                .replace(R.id.fragment_container, PaymentPrefsFragment.newInstance())
                .addToBackStack(null)
                .commit();
            return true;
        });

        findPreference(getString(R.string.scan_settings_key)).setOnPreferenceClickListener(preference -> {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction
                .replace(R.id.fragment_container, ScanSettings.newInstance())
                .addToBackStack(null)
                .commit();
            return true;
        });


        findPreference(getString(R.string.check_in_restrictions_key)).setOnPreferenceClickListener(preference -> {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction
                .replace(R.id.fragment_container, CheckInRestrictions.newInstance(eventId))
                .addToBackStack(null)
                .commit();
            return true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewUtils.setTitle(this, getString(R.string.event_settings));
    }

}
