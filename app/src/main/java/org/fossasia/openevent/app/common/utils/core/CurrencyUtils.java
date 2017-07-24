package org.fossasia.openevent.app.common.utils.core;

import java.util.Currency;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import timber.log.Timber;

/**
 * Source: https://stackoverflow.com/a/15343675/3309666
 */
public class CurrencyUtils {

    private static SortedMap<Currency, Locale> currencyLocaleMap;

    static {
        currencyLocaleMap = new TreeMap<>((c1, c2) -> c1.getCurrencyCode().compareTo(c2.getCurrencyCode()));

        for (Locale locale : Locale.getAvailableLocales()) {
            try {
                Currency currency = Currency.getInstance(locale);
                currencyLocaleMap.put(currency, locale);
            } catch (Exception e){
                Timber.wtf(e);
            }
        }
    }

    public static String getCurrencySymbol(String currencyCode) {
        Currency currency = Currency.getInstance(currencyCode);
        return currency.getSymbol(currencyLocaleMap.get(currency));
    }

}
