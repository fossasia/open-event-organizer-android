package org.fossasia.openevent.app.ui.views;

import org.fossasia.openevent.app.data.event.serializer.ObservableString;

public interface OnDateTimeChangedListener {
    void onDateChanged(ObservableString newDate);
}
