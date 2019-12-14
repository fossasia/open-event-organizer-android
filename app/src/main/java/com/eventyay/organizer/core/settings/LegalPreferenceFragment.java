package com.eventyay.organizer.core.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.ui.ViewUtils;
import com.eventyay.organizer.utils.BrowserUtils;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

public class LegalPreferenceFragment extends PreferenceFragmentCompat {

    public static final String PRIVACY_POLICY_URL = "https://eventyay.com/privacy-policy";
    public static final String REFUND_POLICY_URL = "https://eventyay.com/refunds";
    public static final String TERMS_OF_USE_URL = "https://eventyay.com/terms";

    public static LegalPreferenceFragment newInstance() {
        return new LegalPreferenceFragment();
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

        setPreferencesFromResource(R.xml.legal_preferences, rootKey);

        findPreference(getString(R.string.privacy_policy_key)).setOnPreferenceClickListener(preference -> {
            BrowserUtils.launchUrl(getContext(), PRIVACY_POLICY_URL);
            return true;
        });

        findPreference(getString(R.string.terms_of_service_key)).setOnPreferenceClickListener(preference -> {
            BrowserUtils.launchUrl(getContext(), TERMS_OF_USE_URL);
            return true;
        });

        findPreference(getString(R.string.refund_policy_key)).setOnPreferenceClickListener(preference -> {
            BrowserUtils.launchUrl(getContext(), REFUND_POLICY_URL);
            return true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewUtils.setTitle(this, getString(R.string.legal));
    }
}
