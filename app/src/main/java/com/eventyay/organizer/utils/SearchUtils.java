package com.eventyay.organizer.utils;

import java.util.Locale;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public final class SearchUtils {

    private static final int MATCH_THRESHOLD = 60;

    private SearchUtils() {
        // Never Called
    }

    /**
     * Token sort partial ratio returns a value in percentage about similarity
     * between two strings. The same is tested and found that
     * greater than 60 does well.
     *
     * @param query to be search
     * @param keys set of strings use to filter the item
     * @return boolean saying filer or not
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis") // Bug in PMD related to DU anomaly
    public static boolean filter(String query, String... keys) {
        String queryNormalized = query.trim().toLowerCase(Locale.getDefault());
        for (String key : keys) {
            if (FuzzySearch.tokenSortPartialRatio(queryNormalized, key.trim().toLowerCase(Locale.getDefault())) > MATCH_THRESHOLD)
                return false;
        }
        return true;
    }

}
