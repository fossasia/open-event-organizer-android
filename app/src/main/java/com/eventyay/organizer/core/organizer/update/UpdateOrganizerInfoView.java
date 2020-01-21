package com.eventyay.organizer.core.organizer.update;

import com.eventyay.organizer.common.Function;
import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Successful;
import com.eventyay.organizer.data.user.User;
import com.google.android.material.textfield.TextInputLayout;

public interface UpdateOrganizerInfoView extends Progressive, Erroneous, Successful {

    void dismiss();

    void validate(TextInputLayout textInputLayout, Function<String, Boolean> function, String str);

    void setUser(User user);

    void backPressed();
}
