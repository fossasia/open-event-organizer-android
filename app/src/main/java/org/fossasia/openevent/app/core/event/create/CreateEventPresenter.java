package org.fossasia.openevent.app.core.event.create;

import org.fossasia.openevent.app.common.mvp.presenter.AbstractBasePresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.event.EventRepository;
import org.fossasia.openevent.app.utils.CurrencyUtils;
import org.fossasia.openevent.app.utils.DateUtils;
import org.fossasia.openevent.app.utils.StringUtils;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneous;

public class CreateEventPresenter extends AbstractBasePresenter<CreateEventView> {

    private final EventRepository eventRepository;
    private Event event = new Event();
    private final Map<String, String> countryCurrencyMap;
    private final List<String> countryList;
    private final List<String> currencyCodesList;

    @Inject
    public CreateEventPresenter(EventRepository eventRepository, CurrencyUtils currencyUtils) {
        this.eventRepository = eventRepository;
        LocalDateTime current = LocalDateTime.now();

        String isoDate = DateUtils.formatDateToIso(current);
        event.setStartsAt(isoDate);
        event.setEndsAt(isoDate);

        countryCurrencyMap = currencyUtils.getCountryCurrencyMap();
        countryList = new ArrayList<>(countryCurrencyMap.keySet());
        currencyCodesList = currencyUtils.getCurrencyCodesList();
    }

    @Override
    public void start() {
        getView().attachCountryList(countryList);
        getView().attachCurrencyCodesList(currencyCodesList);

        List<String> timeZoneList = getView().getTimeZoneList();
        getView().setDefaultTimeZone(
            timeZoneList.indexOf(TimeZone.getDefault().getID())
        );

        getView().setDefaultCountry(
            countryList.indexOf(Locale.getDefault().getDisplayCountry())
        );
    }

    public Event getEvent() {
        return event;
    }

    private boolean verify() {
        try {
            ZonedDateTime start = DateUtils.getDate(event.getStartsAt());
            ZonedDateTime end = DateUtils.getDate(event.getEndsAt());

            if (!end.isAfter(start)) {
                getView().showError("End time should be after start time");
                return false;
            }
            return true;
        } catch (DateTimeParseException pe) {
            getView().showError("Please enter date in correct format");
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

        eventRepository
            .createEvent(event)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .subscribe(createdEvent -> {
                getView().onSuccess("Event Created Successfully");
                getView().close();
            }, Logger::logError);
    }

    private void showEvent() {
        getView().setEvent(event);
    }

    //Used for loading the event information on start
    public void loadEvents(long eventId) {
        eventRepository
            .getEvent(eventId, false)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .doFinally(this::showEvent)
            .subscribe(loadedEvent -> this.event = (Event) loadedEvent, Logger::logError);
    }

    //method called for updating an event
    public void updateEvent() {
        if (!verify())
            return;

        nullifyEmptyFields(event);

        eventRepository
            .updateEvent(event)
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneous(getView()))
            .subscribe(updatedEvent -> {
                getView().onSuccess("Event Updated Successfully");
                getView().close();
            }, Logger::logError);
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
    void onPaymentCountrySelected(String paymentCountry) {
        event.setPaymentCountry(paymentCountry);
        String paymentCurrency = countryCurrencyMap.get(paymentCountry);
        event.setPaymentCurrency(paymentCurrency);
        getView().setPaymentCurrency(currencyCodesList.indexOf(paymentCurrency));
    }
    
    public String getShareableInformation() {
        String doubleLineBreak = "\n\n";
        StringBuilder data = new StringBuilder(20);
        data.append(event.getName())
            .append(doubleLineBreak)
            .append("Starts: ").append(DateUtils.formatDateWithDefault(DateUtils.FORMAT_DAY_COMPLETE, event.getStartsAt()))
            .append(doubleLineBreak)
            .append("Ends at: ").append(DateUtils.formatDateWithDefault(DateUtils.FORMAT_DAY_COMPLETE, event.getEndsAt()));

        if (event.getExternalEventUrl() != null) {
            data.append(doubleLineBreak).append("Url: ").append(event.getExternalEventUrl());
        }

        return data.toString();
    }
}
