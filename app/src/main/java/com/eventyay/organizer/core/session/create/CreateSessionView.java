package com.eventyay.organizer.core.session.create;

import com.google.android.material.textfield.TextInputLayout;

import com.eventyay.organizer.common.Function;
import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Successful;
import com.eventyay.organizer.data.session.Session;

public interface CreateSessionView extends Progressive, Erroneous, Successful {

    void validate(TextInputLayout textInputLayout, Function<String, Boolean> function, String str);

    void setSession(Session session);

    void dismiss();
}
