package org.fossasia.openevent.app.ui.views;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import org.fossasia.openevent.app.utils.DateUtils;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

public class DatePicker extends AbstractDateTimePicker {
    public DatePicker(Context context) {
        super(context);

    }

    public DatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public DatePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public void setValue(String value) {
        if (getValue().get() == null || !TextUtils.equals(getValue().get(), value)) {
            getValue().set(value);
            String format = DateUtils.FORMAT_DATE_COMPLETE;

            bindTemporal(value, format, zonedDateTime ->
                new DatePickerDialog(this.getContext(), (picker, year, month, dayOfMonth) ->
                    setPickedDate(
                        LocalDateTime.of(LocalDate.of(year, month + 1, dayOfMonth), zonedDateTime.toLocalTime()), format),
                    zonedDateTime.getYear(), zonedDateTime.getMonthValue() - 1, zonedDateTime.getDayOfMonth()));
        }
    }
}
