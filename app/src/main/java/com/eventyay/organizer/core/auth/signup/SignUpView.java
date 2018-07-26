package com.eventyay.organizer.core.auth.signup;

import android.support.design.widget.TextInputLayout;

import com.eventyay.organizer.common.Function;
import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Successful;

public interface SignUpView extends Progressive, Successful, Erroneous {

    void validate(TextInputLayout textInputLayout, Function<String, Boolean> function, String str);
}
