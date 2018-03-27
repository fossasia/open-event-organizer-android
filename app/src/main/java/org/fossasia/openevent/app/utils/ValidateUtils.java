package org.fossasia.openevent.app.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ValidateUtils {

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static final Pattern VALID_URL_REGEX =
        Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    private ValidateUtils() {
    }

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    public static boolean validateUrl(String urlStr) {
        Matcher matcher = VALID_URL_REGEX .matcher(urlStr);
        return matcher.find();
    }
}
