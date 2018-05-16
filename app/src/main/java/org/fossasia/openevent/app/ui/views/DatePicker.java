package org.fossasia.openevent.app.ui.views;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import org.fossasia.openevent.app.common.Function;
import org.fossasia.openevent.app.data.event.serializer.ObservableString;
import org.fossasia.openevent.app.utils.DateUtils;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeParseException;

import timber.log.Timber;

public class DatePicker extends android.support.v7.widget.AppCompatButton {
    private final ObservableString value = new ObservableString();
    private OnDateChangedListener onDateChangedListener;

    public DatePicker(Context context) {
        super(context);
        init();
    }

    public DatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DatePicker(Context context, AttributeSet attrs, int defStyle) {
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
            String format = DateUtils.FORMAT_DATE_COMPLETE;

            bindTemporal(value, format, zonedDateTime ->
                new DatePickerDialog(this.getContext(), (picker, year, month, dayOfMonth) ->
                    setPickedDate(
                        LocalDateTime.of(LocalDate.of(year, month + 1, dayOfMonth), zonedDateTime.toLocalTime()), format),
                    zonedDateTime.getYear(), zonedDateTime.getMonthValue() - 1, zonedDateTime.getDayOfMonth()));
        }
    }

    // change listener

    public interface OnDateChangedListener {
        void onDateChanged(ObservableString newDate);
    }

    public void setOnDateChangedListener(OnDateChangedListener onDateChangedListener) {
        this.onDateChangedListener = onDateChangedListener;
    }
}
