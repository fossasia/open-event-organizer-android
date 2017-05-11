package org.fossasia.openevent.app.Utils;


public class Constants {
    //url to login organiser
    public static final String LOGIN_URL = "http://open-event-dev.herokuapp.com/api/v1/login";
    //Logged in user events
    public static final String userEvents = "http://open-event-dev.herokuapp.com/api/v1/users/me/events";
    //users events api
    public static final String userDetails = "http://open-event-dev.herokuapp.com/api/v1/users";
    //event details
    public static final String eventDetails = "http://open-event-dev.herokuapp.com/api/v1/events/";
    // attendees toggle
    public static final String attendeesToggle = "/attendees/check_in_toggle/";
    //event details tickets
    public static final String tickets = "?include=tickets";
    //Attendees details
    public static final String attendees = "/attendees";
    //SharedPrefs key
    public static final String sharedPrefsToken = "token";
    //SharedPrefs db Name
    public static final String fossPrefs = "FossPrefs";
    //ScanQR identifier
    public static final String scannedIdentifier = "identifier";
    //ScanQR id
    public static final String scannedId = "id";
    //attendee cheching in
    public static final String attendeeChechingIn = "Checking In";
    //attendee checking Out
    public static final String AttendeeCheckingOut = "Cheching Out";
    //attende scanned index
    public static final String scannedIndex = "index";
    //No network available string
    public static final String noNetwork = "Network Not Available";

}
