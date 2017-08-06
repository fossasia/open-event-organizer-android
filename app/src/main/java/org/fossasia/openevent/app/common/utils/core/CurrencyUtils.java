package org.fossasia.openevent.app.common.utils.core;

import java.util.Currency;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Source: https://stackoverflow.com/a/15343675/3309666
 */
public final class CurrencyUtils {

    private CurrencyUtils() {
        // Never Called
    }

    private static SortedMap<Currency, Locale> currencyLocaleMap;

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

}
