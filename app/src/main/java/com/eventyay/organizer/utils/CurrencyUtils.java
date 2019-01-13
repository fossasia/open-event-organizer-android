package com.eventyay.organizer.utils;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Source: https://stackoverflow.com/a/15343675/3309666
 */
public final class CurrencyUtils {

    private SortedMap<Currency, Locale> currencyLocaleMap;
    private Map<String, String> countryCurrencyMap;

    @Inject
    public CurrencyUtils() {
        // Never Called
    }

    @SuppressWarnings({ "PMD.AvoidCatchingGenericException", "PMD.EmptyCatchBlock" })
    private Single<SortedMap<Currency, Locale>> getCurrecyMap() {
        if (currencyLocaleMap != null && !currencyLocaleMap.isEmpty())
            return Single.just(currencyLocaleMap);

        return Single.fromCallable(() -> {
            currencyLocaleMap = new TreeMap<>((c1, c2) -> c1.getCurrencyCode().compareTo(c2.getCurrencyCode()));

            for (Locale locale : Locale.getAvailableLocales()) {
                try {
                    Currency currency = Currency.getInstance(locale);
                    currencyLocaleMap.put(currency, locale);
                } catch (Exception e) {
                    // No action
                }
            }
            return currencyLocaleMap;
        });
    }

    public Single<String> getCurrencySymbol(String currencyCode) {
        if (currencyCode == null)
            return Single.error(new Throwable("Currency Code is null"));

        return Single
            .just(currencyCode)
            .map(Currency::getInstance)
            .zipWith(
                getCurrecyMap(),
                (currency, currencyLocalMap) ->
                    currency.getSymbol(currencyLocalMap.get(currency))
            )
            .subscribeOn(Schedulers.computation());
    }

    @SuppressWarnings({ "PMD.UseConcurrentHashMap"})
    public Map<String, String> getCountryCurrencyMap() {

        if (countryCurrencyMap != null)
            return countryCurrencyMap;

        countryCurrencyMap = new TreeMap<>();
        for (Locale locale : Locale.getAvailableLocales()) {
            try {
                String country = locale.getDisplayCountry();
                Currency currency = null;
                if( !country.equals("")&&!locale.toString().matches(".*\\d+.*"))
                    currency = Currency.getInstance(locale);
                if (currency != null && country != null) {
                    countryCurrencyMap.put(country, currency.toString());

                }
            } catch (IllegalArgumentException e) {

                Timber.d(e);
            }
        }
        return countryCurrencyMap;
    }

    public List<String> getCurrencyCodesList() {
        List<String> priorCurrencyCodes = new ArrayList<>();

        priorCurrencyCodes.add(Currency.getInstance(Locale.US).toString());
        priorCurrencyCodes.add(Currency.getInstance(Locale.UK).toString());
        priorCurrencyCodes.add(Currency.getInstance(Locale.GERMANY).toString());
        priorCurrencyCodes.add(Currency.getInstance(Locale.JAPAN).toString());
        priorCurrencyCodes.add(Currency.getInstance(Locale.CANADA).toString());
        priorCurrencyCodes.add(Currency.getInstance(Locale.CHINA).toString());
        priorCurrencyCodes.add(Currency.getInstance(Locale.KOREA).toString());

        priorCurrencyCodes.addAll(new ArrayList<String>(new TreeSet<>(getCountryCurrencyMap().values())));

        return priorCurrencyCodes;
    }

}
