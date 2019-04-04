package com.eventyay.organizer.core.settings;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import com.eventyay.organizer.BuildConfig;
import com.eventyay.organizer.R;
import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.ui.ViewUtils;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String VERSION = "Version";
    private static final String GROSS_SALES = "Gross Sales";
    private static final String NET_SALES = "Net Sales";
    private final AcknowledgementDecider acknowledgementDecider = new AcknowledgementDecider();
    private PreferenceManager manager;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(R.color.color_top_surface));
        return view;
    }

    @Override
    public void onCreatePreferencesFix(@Nullable Bundle bundle, String rootKey) {
        manager = getPreferenceManager();
        manager.setSharedPreferencesName(Constants.FOSS_PREFS);

        setPreferencesFromResource(R.xml.preferences, rootKey);

        findPreference(getString(R.string.sales_data_display_key)).setOnPreferenceClickListener(preference -> {
            getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, SalesDataSettings.newInstance())
                .addToBackStack(null)
                .commit();
            return true;
        });

        findPreference(getString(R.string.app_version_key)).setTitle(VERSION + " " + BuildConfig.VERSION_NAME);

        findPreference("rate_us").setOnPreferenceClickListener(preference -> {
            Uri uri = Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID);
            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, uri);
            try {
                startActivity(playStoreIntent);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
            }
            return true;
        });

        findPreference(getString(R.string.legal_key)).setOnPreferenceClickListener(preference -> {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction
                .replace(R.id.fragment_container, LegalPreferenceFragment.newInstance())
                .addToBackStack(null)
                .commit();
            return true;
        });

        findPreference(getString(R.string.share_app_key)).setOnPreferenceClickListener(preference -> {
            Intent sharingIntent = new Intent();
            sharingIntent.setAction(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareSub = getString(R.string.msg_check_out);
            String shareBody = getString(R.string.msg_check_this_out) + getString(R.string.play_store_link);
            String shareTitle = getString(R.string.msg_share_using);
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, shareTitle));
            return true;
        });

        findPreference(getString(R.string.acknowledgements_key)).setVisible(acknowledgementDecider.shouldShowAcknowledgement());

        findPreference(getString(R.string.acknowledgements_key)).setOnPreferenceClickListener(preference -> {
            acknowledgementDecider.openAcknowledgementsSection(getActivity());
            return true;
        });
    }

    public void setSalesDataSummary() {
        String salesData;

        if (manager.getSharedPreferences().getBoolean(getString(R.string.gross_sales_key), true)) {
            salesData = GROSS_SALES;
        } else {
            salesData = NET_SALES;
        }

        findPreference(getString(R.string.sales_data_display_key)).setSummary(salesData);
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewUtils.setTitle(this, getString(R.string.device_settings));
        setSalesDataSummary();
    }
}
