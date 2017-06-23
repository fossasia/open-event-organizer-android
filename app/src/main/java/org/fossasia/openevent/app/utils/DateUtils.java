package org.fossasia.openevent.app.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
    private DateFormat dateFormat;

    public DateUtils() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public Date parse(String dateString) throws ParseException {
        return dateFormat.parse(dateString);
    }

    public String format(Date date) {
        return dateFormat.format(date);
    }
}
