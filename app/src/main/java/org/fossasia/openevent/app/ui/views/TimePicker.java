package org.fossasia.openevent.app.ui.views;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import org.fossasia.openevent.app.common.Function;
import org.fossasia.openevent.app.data.event.serializer.ObservableString;
import org.fossasia.openevent.app.utils.DateUtils;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeParseException;

import timber.log.Timber;

public class TimePicker extends android.support.v7.widget.AppCompatButton {
    private final ObservableString value = new ObservableString();
    private DatePicker.OnDateChangedListener onDateChangedListener;

    public TimePicker(Context context) {
        super(context);
        init();
    }

    public TimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        LocalDateTime current = LocalDateTime.now();
        String isoDate = DateUtils.formatDateToIso(current);
        setValue(isoDate);
    }

    private void bindTemporal(String date, String format, Function<ZonedDateTime, AlertDialog> dialogProvider) {
        if (date == null)
            return;

        this.setText(DateUtils.formatDateWithDefault(format, date));

        this.setOnClickListener(view -> {
            ZonedDateTime zonedDateTime = ZonedDateTime.now();
            try {
                zonedDateTime = DateUtils.getDate(date);
            } catch (DateTimeParseException pe) {
                Timber.e(pe);
            }
            dialogProvider.apply(zonedDateTime).show();
        });
    }

    private void setPickedDate(LocalDateTime pickedDate, String format) {
        String isoDate = DateUtils.formatDateToIso(pickedDate);
        this.value.set(isoDate);
        String formattedDate = DateUtils.formatDateWithDefault(format, isoDate);
        this.setText(formattedDate);
        if (onDateChangedListener != null) {
            onDateChangedListener.onDateChanged(value);
        }
    }

    public ObservableString getValue() {
        return value;
    }

    public void setValue(String value) {
        if (this.value.get() == null || !TextUtils.equals(this.value.get(), value)) {
            this.value.set(value);
            String format = DateUtils.FORMAT_24H;

            bindTemporal(value, format, zonedDateTime ->
                new TimePickerDialog(this.getContext(), (picker, hourOfDay, minute) ->
                    setPickedDate(
                        LocalDateTime.of(zonedDateTime.toLocalDate(), LocalTime.of(hourOfDay, minute)), format),
                    zonedDateTime.getHour(), zonedDateTime.getMinute(), true));
        }
    }

    public void setOnDateChangedListener(DatePicker.OnDateChangedListener onDateChangedListener) {
        this.onDateChangedListener = onDateChangedListener;
    }
}
