package org.fossasia.openevent.app.core.event.copyright;

import android.support.design.widget.TextInputLayout;

import org.fossasia.openevent.app.common.Function;
import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Successful;

public interface CreateCopyrightView extends Progressive, Erroneous, Successful {

    void dismiss();

    void validate(TextInputLayout textInputLayout, Function<String, Boolean> function, String str);
}
