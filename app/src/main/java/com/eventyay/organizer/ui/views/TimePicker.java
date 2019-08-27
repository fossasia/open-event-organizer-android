package com.eventyay.organizer.ui.views;

import android.app.TimePickerDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.eventyay.organizer.data.event.serializer.ObservableString;
import com.eventyay.organizer.utils.DateUtils;
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
        ObservableString observableValue = getValue();
        if (observableValue.get() == null || !TextUtils.equals(observableValue.get(), value)) {
            observableValue.set(value);
            String format = DateUtils.FORMAT_24H;

            bindTemporal(
                    value,
                    format,
                    zonedDateTime ->
                            new TimePickerDialog(
                                    this.getContext(),
                                    (picker, hourOfDay, minute) ->
                                            setPickedDate(
                                                    LocalDateTime.of(
                                                            zonedDateTime.toLocalDate(),
                                                            LocalTime.of(hourOfDay, minute)),
                                                    format),
                                    zonedDateTime.getHour(),
                                    zonedDateTime.getMinute(),
                                    true));
        }
    }
}
