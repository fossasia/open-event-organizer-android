package com.eventyay.organizer.core.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;
import com.eventyay.organizer.R;
import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.utils.CurrencyUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CountryPreferenceFragmentCompat extends PreferenceDialogFragmentCompat {

    private Spinner countrySpinner;
    private int index;
    private int savedIndex;

    public static CountryPreferenceFragmentCompat newInstance(String key) {
        final CountryPreferenceFragmentCompat fragmentCompat =
                new CountryPreferenceFragmentCompat();
        final Bundle bundle = new Bundle();
        bundle.putString(Constants.PREF_PAYMENT_COUNTRY, key);
        fragmentCompat.setArguments(bundle);
        return fragmentCompat;
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            DialogPreference preference = getPreference();
            if (preference instanceof CountryPreference) {
                CountryPreference countryPreference = ((CountryPreference) preference);
                countryPreference.setCountry(index);
            }
        }
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        countrySpinner = view.findViewById(R.id.country_spinner);

        DialogPreference preference = getPreference();
        if (preference instanceof CountryPreference)
            savedIndex = ((CountryPreference) preference).getCountry();

        setUpSpinner();
    }

    private void setUpSpinner() {
        CurrencyUtils currencyUtils = new CurrencyUtils();
        Map<String, String> countryCurrencyMap = currencyUtils.getCountryCurrencyMap();
        List<String> countryList = new ArrayList<>(countryCurrencyMap.keySet());

        ArrayAdapter<CharSequence> paymentCountryAdapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item);
        paymentCountryAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        paymentCountryAdapter.addAll(countryList);
        countrySpinner.setAdapter(paymentCountryAdapter);
        countrySpinner.setSelection(savedIndex);

        countrySpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        index = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // empty
                    }
                });
    }
}
