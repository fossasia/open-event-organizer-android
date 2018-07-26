package com.eventyay.organizer.ui.views;

import com.eventyay.organizer.data.event.serializer.ObservableString;

public interface OnDateTimeChangedListener {
    void onDateChanged(ObservableString newDate);
}
