package com.eventyay.organizer.ui.views;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;

import com.eventyay.organizer.common.Function;
import com.eventyay.organizer.data.event.serializer.ObservableString;
import com.eventyay.organizer.utils.DateUtils;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeParseException;

import java.text.ParseException;

import timber.log.Timber;

public abstract class AbstractDateTimePicker extends androidx.appcompat.widget.AppCompatButton {
    private final ObservableString value = new ObservableString();
    private OnDateTimeChangedListener onDateChangedListener;
    private LocalDateTime current;

    public AbstractDateTimePicker(Context context) {
        super(context);
        init();
    }

    public AbstractDateTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AbstractDateTimePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        current = LocalDateTime.now();
        String isoDate = DateUtils.formatDateToIso(current);
        try {
            setValue(isoDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis") // Inevitable DD anomaly
    protected void bindTemporal(String date, String format, Function<ZonedDateTime, AlertDialog> dialogProvider) {
        if (date == null)
            return;

        this.setText(DateUtils.formatDateWithDefault(format, date));

        this.setOnClickListener(view ->
            dialogProvider.apply(DateUtils.getDate(DateUtils.formatDateToIso(current))).show()
        );
    }

    public void setOnDateChangedListener(OnDateTimeChangedListener onDateChangedListener) {
        this.onDateChangedListener = onDateChangedListener;
    }

    public void setPickedDate(LocalDateTime pickedDate, String format) {
        current = pickedDate;
        String isoDate = DateUtils.formatDateToIso(pickedDate);
        this.value.set(isoDate);
        String formattedDate = DateUtils.formatDateWithDefault(format, isoDate);
        this.setText(formattedDate);
        if (onDateChangedListener != null) {
            onDateChangedListener.onDateChanged(value);
        }
    }

    public abstract void setValue(String value) throws ParseException;

    public ObservableString getValue() {
        return value;
    }
}
