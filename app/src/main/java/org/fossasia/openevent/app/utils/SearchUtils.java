package org.fossasia.openevent.app.utils;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class SearchUtils {

    /**
     * Token sort partial ratio returns a value in percentage about similarity
     * between two strings. The same is tested and found that
     * greater than 60 does well.
     *
     * @param query to be search
     * @param keys set of strings use to filter the item
     * @return boolean saying filer or not
     */
    public static boolean shallFilter(String query, String... keys) {
        for(String key:keys) {
            if (FuzzySearch.tokenSortPartialRatio(query.trim().toLowerCase(), key.trim().toLowerCase()) > 60) {
                return false;
            }
        }
        return true;
    }

}
