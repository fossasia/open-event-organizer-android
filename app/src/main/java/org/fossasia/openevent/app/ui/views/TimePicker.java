package org.fossasia.openevent.app.ui.views;

import android.app.TimePickerDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import org.fossasia.openevent.app.utils.DateUtils;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

public class TimePicker extends AbstractDateTimePicker {
    public TimePicker(Context context) {
        super(context);
    }

    public TimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setValue(String value) {
        if (getValue().get() == null || !TextUtils.equals(getValue().get(), value)) {
            getValue().set(value);
            String format = DateUtils.FORMAT_24H;

            bindTemporal(value, format, zonedDateTime ->
                new TimePickerDialog(this.getContext(), (picker, hourOfDay, minute) ->
                    setPickedDate(
                        LocalDateTime.of(zonedDateTime.toLocalDate(), LocalTime.of(hourOfDay, minute)), format),
                    zonedDateTime.getHour(), zonedDateTime.getMinute(), true));
        }
    }
}
