package com.eventyay.organizer.core.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceManager;
import com.eventyay.organizer.R;
import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.ui.ViewUtils;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

public class ScanSettings extends PreferenceFragmentCompat {

    public static ScanSettings newInstance() {
        return new ScanSettings();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(R.color.color_top_surface));
        return view;
    }

    @Override
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis") // Inevitable DU Anomaly
    public void onCreatePreferencesFix(@Nullable Bundle bundle, String rootKey) {
        PreferenceManager manager = getPreferenceManager();
        manager.setSharedPreferencesName(Constants.FOSS_PREFS);

        setPreferencesFromResource(R.xml.scan_settings, rootKey);

        CheckBoxPreference checkOut =
                (CheckBoxPreference) findPreference(Constants.PREF_SCAN_WILL_CHECK_OUT);
        CheckBoxPreference validate =
                (CheckBoxPreference) findPreference(Constants.PREF_SCAN_WILL_VALIDATE);
        CheckBoxPreference checkIn =
                (CheckBoxPreference) findPreference(Constants.PREF_SCAN_WILL_CHECK_IN);

        OnPreferenceChangeListener listener =
                (preference, newValue) -> {
                    String key = preference.getKey();

                    switch (key) {
                        case Constants.PREF_SCAN_WILL_CHECK_IN:
                            // Reset other items
                            checkOut.setChecked(false);
                            validate.setChecked(false);
                            break;
                        case Constants.PREF_SCAN_WILL_CHECK_OUT:
                            // Reset other items
                            checkIn.setChecked(false);
                            validate.setChecked(false);
                            break;
                        case Constants.PREF_SCAN_WILL_VALIDATE:
                            // Reset other items
                            checkOut.setChecked(false);
                            checkIn.setChecked(false);
                            break;
                        default:
                            break;
                    }

                    /*
                    Force the current focused checkbox to always stay checked when pressed
                    i.e confirms value when newValue is checked (true) and discards newValue
                    when newValue is unchecked (false)
                    */
                    return (Boolean) newValue;
                };

        checkIn.setOnPreferenceChangeListener(listener);
        checkOut.setOnPreferenceChangeListener(listener);
        validate.setOnPreferenceChangeListener(listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewUtils.setTitle(this, getString(R.string.scan_settings));
    }
}
