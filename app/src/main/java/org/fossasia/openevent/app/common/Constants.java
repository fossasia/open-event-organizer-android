package org.fossasia.openevent.app.common;

public final class Constants {

    private Constants() {
        // Never Called
    }

    //url to login organiser
    public static final String BASE_URL = "https://open-event-api.herokuapp.com/v1/";
    public static final String SHARED_PREFS_TOKEN = "token";
    //SharedPrefs db Name
    public static final String FOSS_PREFS = "FossPrefs";
    //No network available string
    public static final String NO_NETWORK = "Network Not Available";
    //Saved email for autocomplete
    public static final String SHARED_PREFS_SAVED_EMAIL = "saved_email";
    public static final String SHARED_PREFS_LOCAL_DATE = "local_date";
}
