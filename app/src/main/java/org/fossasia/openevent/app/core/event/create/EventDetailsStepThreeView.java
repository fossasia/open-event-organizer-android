package org.fossasia.openevent.app.core.event.create;

import android.support.design.widget.TextInputLayout;

import org.fossasia.openevent.app.common.Function;
import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Successful;
import org.fossasia.openevent.app.data.event.Event;

import java.util.List;

public interface EventDetailsStepThreeView extends Progressive, Erroneous, Successful {

    void attachCountryList(List<String> countryList);

    void attachCurrencyCodesList(List<String> currencyCodesList);

    void setPaymentCurrency(int index);

    void setPaymentBinding(Event event);

    void validate(TextInputLayout textInputLayout, Function<String, Boolean> fuck, String tsr);

}
