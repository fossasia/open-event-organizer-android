package com.eventyay.organizer.core.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.ui.ViewUtils;

import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

public class LegalPreferenceFragment extends PreferenceFragmentCompat {

    public static final String PRIVACY_POLICY_URL = "https://eventyay.com/privacy-policy/";
    public static final String COOKIE_POLICY_URL = "https://eventyay.com/cookie-policy/";
    public static final String TERMS_OF_USE_URL = "https://eventyay.com/terms/";

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
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(PRIVACY_POLICY_URL));
            startActivity(intent);
            return true;
        });

        findPreference(getString(R.string.terms_of_service_key)).setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(TERMS_OF_USE_URL));
            startActivity(intent);
            return true;
        });

        findPreference(getString(R.string.cookie_policy_key)).setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(COOKIE_POLICY_URL));
            startActivity(intent);
            return true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewUtils.setTitle(this, getString(R.string.legal));
    }
}
