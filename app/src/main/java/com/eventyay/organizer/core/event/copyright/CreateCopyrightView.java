package com.eventyay.organizer.core.event.copyright;

import com.eventyay.organizer.common.Function;
import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Successful;
import com.google.android.material.textfield.TextInputLayout;

public interface CreateCopyrightView extends Progressive, Erroneous, Successful {

    void dismiss();

    void validate(TextInputLayout textInputLayout, Function<String, Boolean> function, String str);
}
