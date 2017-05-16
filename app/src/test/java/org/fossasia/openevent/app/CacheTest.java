package org.fossasia.openevent.app;

import org.fossasia.openevent.app.data.cache.ObjectCache;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CacheTest {

    private ObjectCache objectCache = new ObjectCache();

    @Test
    public void testStore() {
        objectCache.saveObject(1, 2);

        assertEquals(2, objectCache.getValue(1));
    }

}
