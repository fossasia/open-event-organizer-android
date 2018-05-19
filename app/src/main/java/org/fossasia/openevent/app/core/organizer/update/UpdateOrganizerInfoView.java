package org.fossasia.openevent.app.core.organizer.update;

import com.google.android.material.textfield.TextInputLayout;

import org.fossasia.openevent.app.common.Function;
import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Successful;
import org.fossasia.openevent.app.data.user.User;

public interface UpdateOrganizerInfoView extends Progressive, Erroneous, Successful {

    void dismiss();

    void validate(com.google.android.material.textfield.TextInputLayout textInputLayout, Function<String, Boolean> function, String str);

    void setUser(User user);
}
