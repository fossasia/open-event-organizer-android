package org.fossasia.openevent.app.core.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.ui.ViewUtils;

public class SalesDataSettings extends PreferenceFragmentCompat {

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

        CheckBoxPreference netSales = (CheckBoxPreference) findPreference("net_sales");
        CheckBoxPreference grossSales = (CheckBoxPreference) findPreference("gross_sales");

        Preference.OnPreferenceChangeListener listener = (preference, newValue) -> {
            String key = preference.getKey();

            switch (key) {
                case "gross_sales":
                    netSales.setChecked(false);
                    break;
                case "net_sales":
                    grossSales.setChecked(false);
                    break;
                default:
                    break;
            }
            return (Boolean) newValue;
        };

        grossSales.setOnPreferenceChangeListener(listener);
        netSales.setOnPreferenceChangeListener(listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewUtils.setTitle(this, getString(R.string.sales_data_display));
    }

}
