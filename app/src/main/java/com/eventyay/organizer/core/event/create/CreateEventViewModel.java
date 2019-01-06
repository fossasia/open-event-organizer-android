package com.eventyay.organizer.core.event.create;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.Preferences;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.event.EventRepository;
import com.eventyay.organizer.data.event.ImageData;
import com.eventyay.organizer.data.event.ImageUrl;
import com.eventyay.organizer.utils.CurrencyUtils;
import com.eventyay.organizer.utils.DateUtils;
import com.eventyay.organizer.utils.ErrorUtils;
import com.eventyay.organizer.utils.StringUtils;
import com.eventyay.organizer.utils.Utils;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

import static com.eventyay.organizer.common.Constants.PREF_ACCEPT_BANK_TRANSFER;
import static com.eventyay.organizer.common.Constants.PREF_ACCEPT_CHEQUE;
import static com.eventyay.organizer.common.Constants.PREF_ACCEPT_PAYPAL;
import static com.eventyay.organizer.common.Constants.PREF_ACCEPT_STRIPE;
import static com.eventyay.organizer.common.Constants.PREF_BANK_DETAILS;
import static com.eventyay.organizer.common.Constants.PREF_CHEQUE_DETAILS;
import static com.eventyay.organizer.common.Constants.PREF_PAYMENT_ACCEPT_ONSITE;
import static com.eventyay.organizer.common.Constants.PREF_PAYMENT_ONSITE_DETAILS;
import static com.eventyay.organizer.common.Constants.PREF_PAYPAL_EMAIL;
import static com.eventyay.organizer.common.Constants.PREF_USE_PAYMENT_PREFS;


public class CreateEventViewModel extends ViewModel {

    private final EventRepository eventRepository;
    private final Preferences preferences;
    private final Map<String, String> countryCurrencyMap;
    private final List<String> countryList;
    private final List<String> currencyCodesList;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final MutableLiveData<String> onSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> onError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> progress = new MutableLiveData<>();
    private final MutableLiveData<Void> close = new MutableLiveData<>();
    private final MutableLiveData<Event> eventMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<ImageUrl> imageUrlMutableLiveData = new MutableLiveData<>();

    private Event event = new Event();

    @Inject
    public CreateEventViewModel(EventRepository eventRepository, CurrencyUtils currencyUtils, Preferences preferences) {
        this.eventRepository = eventRepository;
        this.preferences = preferences;
        LocalDateTime current = LocalDateTime.now();

        String isoDate = DateUtils.formatDateToIso(current);
        event.setStartsAt(isoDate);
        event.setEndsAt(isoDate);

        countryCurrencyMap = currencyUtils.getCountryCurrencyMap();
        countryList = new ArrayList<>(countryCurrencyMap.keySet());
        currencyCodesList = currencyUtils.getCurrencyCodesList();

        setPaymentPreferences(preferences);

    }

    public int setTimeZoneList(List<String> timeZoneList) {
        return timeZoneList.indexOf(TimeZone.getDefault().getID());
    }

    public List<String> getCountryList() {
        return countryList;
    }

    public List<String> getCurrencyCodesList() {
        return currencyCodesList;
    }

    public Event getEvent() {
        return event;
    }

    public LiveData<Event> getEventLiveData() {
        eventMutableLiveData.setValue(event);
        return eventMutableLiveData;
    }

    public LiveData<ImageUrl> getImageUrlLiveData() {
        return imageUrlMutableLiveData;
    }

    public boolean verify() {
        if (Utils.isEmpty(event.getName())) {
            onError.setValue("Event Name cannot be empty");
            return false;
        }
        try {
            ZonedDateTime start = DateUtils.getDate(event.getStartsAt());
            ZonedDateTime end = DateUtils.getDate(event.getEndsAt());

            if (!end.isAfter(start)) {
                onError.setValue("End time should be after start time");
                return false;
            }
            return true;
        } catch (DateTimeParseException pe) {
            onError.setValue("Please enter date in correct format");
            return false;
        }

    }

    protected void nullifyEmptyFields(Event event) {
        event.setLogoUrl(StringUtils.emptyToNull(event.getLogoUrl()));
        event.setTicketUrl(StringUtils.emptyToNull(event.getTicketUrl()));
        event.setOriginalImageUrl(StringUtils.emptyToNull(event.getOriginalImageUrl()));
        event.setExternalEventUrl(StringUtils.emptyToNull(event.getExternalEventUrl()));
        event.setPaypalEmail(StringUtils.emptyToNull(event.getPaypalEmail()));
    }

    public void createEvent() {
        if (!verify())
            return;

        nullifyEmptyFields(event);

        compositeDisposable.add(eventRepository
            .createEvent(event)
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(createdEvent -> {
                onSuccess.setValue("Event Created Successfully");
                close.setValue(null);
            }, throwable -> {
                onError.setValue(ErrorUtils.getErrorDetails(throwable).toString());
                Timber.e(throwable, "An exception occurred : %s", throwable.getMessage());
            }));
    }

    public LiveData<String> getSuccessMessage() {
        return onSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return onError;
    }

    public LiveData<Void> getCloseState() {
        return close;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    /**
     * Returns the most accurate and searchable address substring, which a user can search for.
     * Also makes sure that the substring doesn't contain any numbers by matching it to the regex,
     * as those are more likely to be house numbers or block numbers.
     * @param address full address string of a location
     * @return searchable address substring
     */
    public String getSearchableLocationName(String address) {
        String primary = address.substring(0, address.indexOf(','));
        if (primary.matches(".*\\d+.*")) { //contains number => not likely to be searchable
            return address.substring(address.indexOf(',') + 2, address.indexOf(",", address.indexOf(',') + 1));
        } else return primary;
    }

    /**
     * auto-selects paymentCurrency when paymentCountry is selected.
     * @param paymentCountry chosen payment country
     */
    public int onPaymentCountrySelected(String paymentCountry) {
        event.setPaymentCountry(paymentCountry);
        String paymentCurrency = countryCurrencyMap.get(paymentCountry);
        event.setPaymentCurrency(paymentCurrency);
        return currencyCodesList.indexOf(paymentCurrency);
    }

    public void setPaymentPreferences(Preferences preferences) {
        if (preferences.getBoolean(PREF_USE_PAYMENT_PREFS, false)) {
            event.setCanPayByPaypal(
                preferences.getBoolean(PREF_ACCEPT_PAYPAL, false)
            );
            event.setPaypalEmail(
                preferences.getString(PREF_PAYPAL_EMAIL, null)
            );
            event.setCanPayByStripe(
                preferences.getBoolean(PREF_ACCEPT_STRIPE, false)
            );
            event.setCanPayByBank(
                preferences.getBoolean(PREF_ACCEPT_BANK_TRANSFER, false)
            );
            event.setBankDetails(
                preferences.getString(PREF_BANK_DETAILS, null)
            );
            event.setCanPayByCheque(
                preferences.getBoolean(PREF_ACCEPT_CHEQUE, false)
            );
            event.setChequeDetails(
                preferences.getString(PREF_CHEQUE_DETAILS, null)
            );
            event.setCanPayOnsite(
                preferences.getBoolean(PREF_PAYMENT_ACCEPT_ONSITE, false)
            );
            event.setOnsiteDetails(
                preferences.getString(PREF_PAYMENT_ONSITE_DETAILS, null)
            );
        }
    }

    public int getCountryIndex() {
        if (preferences.getBoolean(PREF_USE_PAYMENT_PREFS, false))
            return preferences.getInt(Constants.PREF_PAYMENT_COUNTRY, 0);
        return countryList.indexOf(Locale.getDefault().getDisplayCountry());
    }

    //Used for loading the event information on start
    public void loadEvents(long eventId) {
        compositeDisposable.add(eventRepository
            .getEvent(eventId, false)
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> {
                progress.setValue(false);
                getEventLiveData();
            })
            .subscribe(loadedEvent -> this.event = loadedEvent, Logger::logError));
    }

    //method called for updating an event
    public void updateEvent() {
        if (!verify())
            return;

        nullifyEmptyFields(event);

        compositeDisposable.add(
            eventRepository
                .updateEvent(event)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .subscribe(updatedEvent -> {
                    onSuccess.setValue("Event Updated Successfully");
                    close.setValue(null);
                }, Logger::logError));
    }

    //Method for storing user uploaded image in temporary location
    public void uploadImage(ImageData imageData) {
        compositeDisposable.add(
            eventRepository
                .uploadEventImage(imageData)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .subscribe(uploadedImage -> {
                    onSuccess.setValue("Image Uploaded Successfully");
                    imageUrlMutableLiveData.setValue(uploadedImage);
                }, Logger::logError));
    }
}
