package org.fossasia.openevent.app.core.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.ui.ViewUtils;

public class LegalPreferenceFragment extends PreferenceFragmentCompat {

    public static final String EVENTYAY_BASE_URL = "https://eventyay.com";
    public final String privacyPolicyUrl = EVENTYAY_BASE_URL + "/privacy-policy/";
    public final String cookiePolicyUrl = EVENTYAY_BASE_URL + "/cookie-policy/";
    public final String termsOfUseUrl = EVENTYAY_BASE_URL + "/terms/";

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
            intent.setData(Uri.parse(privacyPolicyUrl));
            startActivity(intent);
            return true;
        });

        findPreference(getString(R.string.terms_of_service_key)).setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(termsOfUseUrl));
            startActivity(intent);
            return true;
        });

        findPreference(getString(R.string.cookie_policy_key)).setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(cookiePolicyUrl));
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
