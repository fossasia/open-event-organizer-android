package org.fossasia.openevent.app.common.data.models;

import java.util.Collections;
import java.util.Map;

/*
 * This class helps to wrap java objects with custom objects such that users can define
 * their own tags to wrap objects with while serialising in JSON.
*/

public final class CustomObjectWrapper {

    private CustomObjectWrapper() {
    }

    public static <E> Map<String, E> withLabel(String outerWrap, E wrappedObject) {
        return Collections.singletonMap(outerWrap, wrappedObject);
    }
}
