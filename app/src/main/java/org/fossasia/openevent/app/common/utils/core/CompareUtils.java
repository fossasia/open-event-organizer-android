package org.fossasia.openevent.app.common.utils.core;

import org.fossasia.openevent.app.common.contract.Function;

import java.util.Locale;

public final class CompareUtils {

    private CompareUtils() {
        // Never Called
    }

    private static <T> String apply(Function<T, String> mapper, T item) {
        return mapper.apply(item).toLowerCase(Locale.getDefault());
    }

    @SafeVarargs
    public static <T> int compareCascading(T one, T two, Function<T, String>... mappers) {
        for (Function<T, String> mapper : mappers) {
            int current = apply(mapper, one).compareTo(apply(mapper, two));
            if (current != 0)
                return current;
        }
        return 0;
    }

}
