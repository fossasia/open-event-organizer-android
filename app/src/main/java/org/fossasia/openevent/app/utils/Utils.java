package org.fossasia.openevent.app.utils;

/**
 * Pure Android free static utility class
 * No Android specific code should be added
 * All static Android specific utility go into
 * AndroidUtils and others to AndroidUtilModel
 */
public class Utils {

    /**
     * Copy from TextUtils to use out of Android
     * @param str CharSequence to be checked
     * @return boolean denoting if str is null or empty
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static String optionalString(String string) {
        return isEmpty(string) ? "" : string;
    }

    public static String formatOptionalString(String format, String... args) {
        String[] newArgs = new String[args.length];

        for(int i = 0; i < args.length; i++) {
            newArgs[i] = optionalString(args[i]);
        }

        return String.format(format, (Object[]) newArgs);
    }
}
