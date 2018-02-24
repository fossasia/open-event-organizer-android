package org.fossasia.openevent.app.module.event.create.contract;

import android.support.design.widget.TextInputLayout;

import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Erroneous;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Progressive;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Successful;
import org.fossasia.openevent.app.common.contract.Function;

import java.util.List;

public interface ICreateEventView extends Progressive, Erroneous, Successful {

    void validate(TextInputLayout textInputLayout, Function<String, Boolean> fuck, String tsr);

    void attachCurrencyCodesList(List<String> currencyCodesList);

}
