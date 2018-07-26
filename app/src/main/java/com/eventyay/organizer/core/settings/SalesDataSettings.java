package com.eventyay.organizer.core.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.ui.ViewUtils;

public class SalesDataSettings extends PreferenceFragmentCompat {

    private static final String NET_SALES = "net_sales";
    private static final String GROSS_SALES = "gross_sales";

    public static SalesDataSettings newInstance() {
        return new SalesDataSettings();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(R.color.color_top_surface));
        return view;
    }

    @Override
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis") // Inevitable DU Anomaly
    public void onCreatePreferencesFix(@Nullable Bundle bundle, String rootKey) {
        PreferenceManager manager = getPreferenceManager();
        manager.setSharedPreferencesName(Constants.FOSS_PREFS);

        setPreferencesFromResource(R.xml.sales_data_display, rootKey);

        CheckBoxPreference netSales = (CheckBoxPreference) findPreference(NET_SALES);
        CheckBoxPreference grossSales = (CheckBoxPreference) findPreference(GROSS_SALES);

        Preference.OnPreferenceChangeListener listener = (preference, newValue) -> {
            String key = preference.getKey();

            switch (key) {
                case GROSS_SALES:
                    netSales.setChecked(false);
                    break;
                case NET_SALES:
                    grossSales.setChecked(false);
                    break;
                default:
                    break;
            }
            return (Boolean) newValue;
        };

        netSales.setOnPreferenceChangeListener(listener);
        grossSales.setOnPreferenceChangeListener(listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewUtils.setTitle(this, getString(R.string.sales_data_display));
    }
}
