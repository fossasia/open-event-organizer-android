package org.fossasia.openevent.app.common.utils.core;

import io.reactivex.functions.Function;
import timber.log.Timber;

public final class CompareUtils {

    private CompareUtils() {
        // Never Called
    }

    @SafeVarargs
    public static <T> int compareCascading(T one, T two, Function<T, String>... mappers) {
        for (Function<T, String> mapper : mappers) {
            try {
                int current = mapper.apply(one).toLowerCase().compareTo(mapper.apply(two).toLowerCase());
                if (current != 0)
                    return current;
            } catch (Exception e) {
                // No-Op Can't happen
                Timber.wtf(e);
            }
        }
        return 0;
    }

}
