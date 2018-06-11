package org.fossasia.openevent.app.core.session.create;

import android.support.design.widget.TextInputLayout;

import org.fossasia.openevent.app.common.Function;
import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Successful;
import org.fossasia.openevent.app.data.session.Session;

public interface CreateSessionView extends Progressive, Erroneous, Successful {

    void validate(TextInputLayout textInputLayout, Function<String, Boolean> function, String str);

    void setSession(Session session);
}
