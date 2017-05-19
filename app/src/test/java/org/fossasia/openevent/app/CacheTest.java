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
        assertEquals(1, objectCache.getSize());

        objectCache.saveObject(2, 2);
        assertEquals(2, objectCache.getSize());
    }

    @Test
    public void shouldClearCache() {
        objectCache.clear();

        assertEquals(0, objectCache.getSize());

        objectCache.saveObject(2, 2);
        assertEquals(1, objectCache.getSize());

        objectCache.clear();

        assertEquals(0, objectCache.getSize());
    }

    @Test
    public void shouldAvoidDuplicates() {
        objectCache.saveObject(1, 2);

        assertEquals(1, objectCache.getSize());

        objectCache.saveObject(1, 2);
        assertEquals(1, objectCache.getSize());
    }

}
