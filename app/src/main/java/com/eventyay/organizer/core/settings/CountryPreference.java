package com.eventyay.organizer.core.settings;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.preference.DialogPreference;
import android.util.AttributeSet;


import com.eventyay.organizer.R;

public class CountryPreference extends DialogPreference {

    private final int layoutResourceId = R.layout.dialog_payment_country;
    private int savedIndex;

    public CountryPreference(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.preferenceStyle);
        setDialogLayoutResource(R.layout.dialog_payment_country);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        setDialogIcon(null);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setCountry(restorePersistedValue ? getPersistedInt(savedIndex) : (int) defaultValue);
        super.onSetInitialValue(restorePersistedValue, defaultValue);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    public int getCountry() {
        return savedIndex;
    }

    public void setCountry(int index) {
        savedIndex = index;
        persistInt(index);
    }

    public int getDialogLayoutResource() {
        return layoutResourceId;
    }
}
