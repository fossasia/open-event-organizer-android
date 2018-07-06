package org.fossasia.openevent.app.core.event.create;

import android.support.design.widget.TextInputLayout;

import org.fossasia.openevent.app.common.Function;
import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Successful;

public interface EventDetailsStepThreeView extends Progressive, Erroneous, Successful {

    void validate(TextInputLayout textInputLayout, Function<String, Boolean> validation, String str);

}
