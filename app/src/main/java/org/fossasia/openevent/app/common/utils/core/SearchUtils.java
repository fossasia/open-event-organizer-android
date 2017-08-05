package org.fossasia.openevent.app.common.utils.core;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public final class SearchUtils {

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
    public static boolean filter(String query, String... keys) {
        String queryNormalized = query.trim().toLowerCase();
        for(String key : keys) {
            if (FuzzySearch.tokenSortPartialRatio(queryNormalized, key.trim().toLowerCase()) > 60) {
                return false;
            }
        }
        return true;
    }

}
