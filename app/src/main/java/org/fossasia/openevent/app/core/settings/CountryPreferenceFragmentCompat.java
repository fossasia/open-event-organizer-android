package org.fossasia.openevent.app.core.settings;

import android.os.Bundle;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.Constants;
import org.fossasia.openevent.app.utils.CurrencyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CountryPreferenceFragmentCompat extends PreferenceDialogFragmentCompat {

    private Spinner countrySpinner;
    private int index, savedIndex = 0;

    public static CountryPreferenceFragmentCompat newInstance(String key) {
        final CountryPreferenceFragmentCompat fragmentCompat = new CountryPreferenceFragmentCompat();
        final Bundle b = new Bundle(1);
        b.putString(Constants.PREF_PAYMENT_COUNTRY, key);
        fragmentCompat.setArguments(b);
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

        ArrayAdapter<CharSequence> paymentCountryAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item);
        paymentCountryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        paymentCountryAdapter.addAll(countryList);
        countrySpinner.setAdapter(paymentCountryAdapter);
        countrySpinner.setSelection(savedIndex);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                index = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //empty
            }
        });
    }

}
