package com.eventyay.organizer.utils;

import android.support.annotation.NonNull;

import com.eventyay.organizer.common.ContextManager;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import timber.log.Timber;

public final class DateUtils {

    public static final String FORMAT_12H = "hh:mm a";
    public static final String FORMAT_24H = "HH:mm";
    public static final String FORMAT_MONTH = "MMM";
    public static final String FORMAT_DATE_COMPLETE = "EE, dd MMM yyyy";
    public static final String FORMAT_DAY_DATE_TIME = "EEE, MMM d, hh:mm a";
    public static final String FORMAT_DAY_COMPLETE = "HH:mm, EE, dd MMM yyyy";

    private static final String INVALID_DATE = "Invalid Date";
    private static final Map<String, DateTimeFormatter> FORMATTER_MAP = new ConcurrentHashMap<>();

    private static boolean showLocal;

    private DateUtils() {
        // Never Called
    }

    private static DateTimeFormatter getFormatter(@NonNull String format) {
        if (!FORMATTER_MAP.containsKey(format))
            FORMATTER_MAP.put(format, DateTimeFormatter.ofPattern(format));

        return FORMATTER_MAP.get(format);
    }

    // Internal convenience methods to reduce boilerplate

    @NonNull
    private static String formatDate(@NonNull String format, @NonNull ZonedDateTime isoDate) {
        return getFormatter(format).format(isoDate);
    }

    @NonNull
    private static ZoneId getZoneId() {
        if (showLocal || ContextManager.getSelectedEvent() == null)
            return ZoneId.systemDefault();
        else
            return ZoneId.of(ContextManager.getSelectedEvent().getTimezone());
    }

    // Public methods

    public static void setShowLocal(boolean showLocal) {
        DateUtils.showLocal = showLocal;
    }

    @NonNull
    public static String formatDateToIso(@NonNull LocalDateTime date) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(date.atZone(getZoneId()));
    }

    @NonNull
    public static ZonedDateTime getDate(@NonNull String isoDateString) {
        if (isoDateString == null)
            return ZonedDateTime.now();
        return ZonedDateTime.parse(isoDateString).withZoneSameInstant(getZoneId());
    }

    // Currently unused but should be used in future to hide fields if not using default string
    @NonNull
    public static String formatDate(@NonNull String format, @NonNull String isoDateString) {
        return formatDate(format, getDate(isoDateString));
    }

    @NonNull
    public static String formatDateWithDefault(@NonNull String format, @NonNull String isoString, @NonNull String defaultString) {
        try {
            return formatDate(format, isoString);
        } catch (DateTimeParseException pe) {
            Timber.e(pe);
            Timber.e("Error parsing date %s with format %s and default string %s",
                isoString,
                format,
                defaultString);
        }

        return defaultString;
    }

    @NonNull
    public static String formatDateWithDefault(@NonNull String format, @NonNull String isoString) {
        return formatDateWithDefault(format, isoString, INVALID_DATE);
    }

    @NonNull
    public static LocalDateTime getIsoOffsetTimeFromTimestamp(@NonNull String timestamp) {
        return LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

}
