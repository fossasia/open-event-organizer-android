package com.eventyay.organizer.common;

public final class Constants {
    //url to login organiser
    public static final String BASE_URL = "https://open-event-api-dev.herokuapp.com/v1/";
    //SharedPrefs db Name
    public static final String FOSS_PREFS = "FossPrefs";
    //No network available string
    public static final String NO_NETWORK = "Network Not Available";
    //Saved email for autocomplete
    public static final String SHARED_PREFS_SAVED_EMAIL = "saved_email";
    public static final String SHARED_PREFS_LOCAL_DATE = "local_date";
    public static final String SHARED_PREFS_BASE_URL = "base_url";
    public static final String PREF_USE_PAYMENT_PREFS = "use_payment_prefs";
    public static final String PREF_ACCEPT_PAYPAL = "accept_paypal";
    public static final String PREF_PAYPAL_EMAIL = "paypal_email";
    public static final String PREF_ACCEPT_STRIPE = "accept_stripe";
    public static final String PREF_ACCEPT_BANK_TRANSFER = "accept_bank_transfers";
    public static final String PREF_BANK_DETAILS = "bank_details";
    public static final String PREF_ACCEPT_CHEQUE = "accept_cheque";
    public static final String PREF_CHEQUE_DETAILS = "cheque_details";
    public static final String PREF_PAYMENT_ACCEPT_ONSITE = "accept_onsite";
    public static final String PREF_PAYMENT_ONSITE_DETAILS = "onsite_details";
    public static final String PREF_PAYMENT_COUNTRY = "key";
    public static final String PREF_SCAN_WILL_CHECK_IN = "check_in";
    public static final String PREF_SCAN_WILL_CHECK_OUT = "check_out";
    public static final String PREF_SCAN_WILL_VALIDATE = "validate";
    public static final String PREF_USER_EMAIL = "user_email";

    private Constants() {
        // Never Called
    }
}
