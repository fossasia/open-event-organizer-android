package org.fossasia.openevent.app.utils;

public class Constants {
    //url to login organiser
    public static final String BASE_URL = "http://open-event-dev.herokuapp.com/api/v1/";

    //url to login organiser
    public static final String LOGIN_URL = "http://open-event-dev.herokuapp.com/api/v1/login";
    //Logged in user events
    public static final String USER_EVENTS = "http://open-event-dev.herokuapp.com/api/v1/users/me/events";
    //users events api
    public static final String USER_DETAILS = "http://open-event-dev.herokuapp.com/api/v1/users";
    //event details
    public static final String EVENT_DETAILS = "http://open-event-dev.herokuapp.com/api/v1/events/";
    // ATTENDEES toggle
    public static final String ATTENDEES_TOGGLE = "/attendees/check_in_toggle/";
    //event details TICKETS
    public static final String TICKETS = "?include=tickets";
    //Attendees details
    public static final String ATTENDEES = "/attendees";
    //SharedPrefs key
    public static final String SHARED_PREFS_TOKEN = "token";
    //SharedPrefs db Name
    public static final String FOSS_PREFS = "FossPrefs";
    //ScanQR identifier
    public static final String SCANNED_IDENTIFIER = "identifier";
    //ScanQR id
    public static final String SCANNED_ID = "id";
    //attendee checking in
    public static final String ATTENDEE_CHECKING_IN = "Checking In";
    //attendee checking Out
    public static final String ATTENDEE_CHECKING_OUT = "Checking Out";
    //attendee scanned index
    public static final String SCANNED_INDEX = "index";
    //No network available string
    public static final String NO_NETWORK = "Network Not Available";

}
