package com.eventyay.organizer.core.event.create;

import android.support.design.widget.TextInputLayout;

import com.eventyay.organizer.common.Function;

public interface EventDetailsStepTwoView {

    void validate(TextInputLayout textInputLayout, Function<String, Boolean> validation, String str);
}
