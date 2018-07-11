package org.fossasia.openevent.app.core.auth.signup;

import android.support.design.widget.TextInputLayout;

import org.fossasia.openevent.app.common.Function;
import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Successful;

public interface SignUpView extends Progressive, Successful, Erroneous {

    void validate(TextInputLayout textInputLayout, Function<String, Boolean> function, String str);
}
