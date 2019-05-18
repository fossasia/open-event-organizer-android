package com.eventyay.organizer.core.event.create;

import com.google.android.material.textfield.TextInputLayout;

import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Successful;
import com.eventyay.organizer.common.Function;
import com.eventyay.organizer.data.event.Event;

import java.util.List;

public interface CreateEventView extends Progressive, Erroneous, Successful {

    void validate(TextInputLayout textInputLayout, Function<String, Boolean> validation, String str);

    void attachCountryList(List<String> countryList);

    void attachCurrencyCodesList(List<String> currencyCodesList);

    void setPaymentCurrency(int index);

    void setPaymentBinding(Event event);

    List<String> getTimeZoneList();

    void setEvent(Event event);
}
