package org.fossasia.openevent.app.core.event.create;

import android.support.design.widget.TextInputLayout;

import org.fossasia.openevent.app.common.Function;

public interface EventDetailsStepTwoView {

    void validate(TextInputLayout textInputLayout, Function<String, Boolean> fuck, String tsr);
}
