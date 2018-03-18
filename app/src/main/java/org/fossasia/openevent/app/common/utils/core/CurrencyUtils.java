package org.fossasia.openevent.app.common.utils.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Source: https://stackoverflow.com/a/15343675/3309666
 */
public final class CurrencyUtils {

    private static SortedMap<Currency, Locale> currencyLocaleMap;

    private CurrencyUtils() {
        // Never Called
    }

    @SuppressWarnings({ "PMD.AvoidCatchingGenericException", "PMD.EmptyCatchBlock" })
    private static Single<SortedMap<Currency, Locale>> getCurrecyMap() {
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

    public static Single<String> getCurrencySymbol(String currencyCode) {
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

    @SuppressWarnings({ "PMD.AvoidCatchingGenericException", "PMD.EmptyCatchBlock" })
    public static List<String> getCurrencyCodesList() {
        List<String> currencyCodes = new ArrayList<>();
        List<String> priorCurrencyCodes = new ArrayList<>();

        priorCurrencyCodes.add(Currency.getInstance(Locale.US).toString());
        priorCurrencyCodes.add(Currency.getInstance(Locale.UK).toString());
        priorCurrencyCodes.add(Currency.getInstance(Locale.GERMANY).toString());
        priorCurrencyCodes.add(Currency.getInstance(Locale.JAPAN).toString());
        priorCurrencyCodes.add(Currency.getInstance(Locale.CANADA).toString());
        priorCurrencyCodes.add(Currency.getInstance(Locale.CHINA).toString());
        priorCurrencyCodes.add(Currency.getInstance(Locale.KOREA).toString());

        for (Locale locale : Locale.getAvailableLocales()) {
            try {
                Currency currency = Currency.getInstance(locale);
                if (currency != null && !currencyCodes.contains(currency.toString())) {
                    currencyCodes.add(currency.toString());
                }
            } catch (Exception e) {
                // No action
            }
        }

        Collections.sort(currencyCodes);
        priorCurrencyCodes.addAll(currencyCodes);
        return priorCurrencyCodes;
    }

}
