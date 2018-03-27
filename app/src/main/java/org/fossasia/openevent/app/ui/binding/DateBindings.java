package org.fossasia.openevent.app.ui.binding;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.widget.Button;

import org.fossasia.openevent.app.common.Function;
import org.fossasia.openevent.app.utils.DateUtils;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeParseException;

import timber.log.Timber;

public final class DateBindings {

    private DateBindings() {
        // Never Called
    }

    private static void setPickedDate(LocalDateTime pickedDate, Button button, String format,
                                      ObservableField<String> date) {
        String isoDate = DateUtils.formatDateToIso(pickedDate);
        date.set(isoDate);
        button.setText(DateUtils.formatDateWithDefault(format, isoDate));
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    private static void bindTemporal(Button button, ObservableField<String> date, String format,
                                     Function<ZonedDateTime, AlertDialog> dialogProvider) {
        if (date == null)
            return;

        String isoDate = date.get();
        button.setText(DateUtils.formatDateWithDefault(format, isoDate));

        button.setOnClickListener(view -> {
            ZonedDateTime zonedDateTime = ZonedDateTime.now();
            try {
                zonedDateTime = DateUtils.getDate(isoDate);
            } catch (DateTimeParseException pe) {
                Timber.e(pe);
            }
            dialogProvider.apply(zonedDateTime).show();
        });
    }

    @BindingAdapter("date")
    public static void bindDate(Button button, ObservableField<String> date) {
        String format = DateUtils.FORMAT_DATE_COMPLETE;

        bindTemporal(button, date, format, zonedDateTime ->
            new DatePickerDialog(button.getContext(), (picker, year, month, dayOfMonth) ->
                setPickedDate(
                    LocalDateTime.of(LocalDate.of(year, month + 1, dayOfMonth), zonedDateTime.toLocalTime()),
                    button, format, date),
                zonedDateTime.getYear(), zonedDateTime.getMonthValue() - 1, zonedDateTime.getDayOfMonth()));
    }

    @BindingAdapter("time")
    public static void bindTime(Button button, ObservableField<String> time) {
        String format = DateUtils.FORMAT_24H;

        bindTemporal(button, time, format, zonedDateTime ->
            new TimePickerDialog(button.getContext(), (picker, hourOfDay, minute) ->
                setPickedDate(
                    LocalDateTime.of(zonedDateTime.toLocalDate(), LocalTime.of(hourOfDay, minute)),
                    button, format, time),
                zonedDateTime.getHour(), zonedDateTime.getMinute(), true));
    }

}
