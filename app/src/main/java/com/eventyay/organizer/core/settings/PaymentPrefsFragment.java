package com.eventyay.organizer.core.settings;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.Constants;

public class PaymentPrefsFragment extends PreferenceFragmentCompat {

    public static PaymentPrefsFragment newInstance() {
        return new PaymentPrefsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(Color.WHITE);

        return view;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceManager manager = getPreferenceManager();
        manager.setSharedPreferencesName(Constants.FOSS_PREFS);

        setPreferencesFromResource(R.xml.payment_preferences, rootKey);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        CountryPreferenceFragmentCompat dialogFragment = null;
        if (preference instanceof CountryPreference)
            dialogFragment = CountryPreferenceFragmentCompat.newInstance(Constants.PREF_PAYMENT_COUNTRY);

        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 1);
            dialogFragment.show(this.getFragmentManager(),
                "android.support.v7.preference" +
                    ".PreferenceFragment.DIALOG");
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}

