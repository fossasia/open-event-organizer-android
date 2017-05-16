package org.fossasia.openevent.app.data.cache;


import android.support.v4.util.LruCache;

import org.fossasia.openevent.app.contract.model.CacheModel;

public class ObjectCache implements CacheModel {

    private static ObjectCache instance;
    private LruCache<Object, Object> lruCache = new LruCache<>(1024);

    public static ObjectCache getInstance() {
        if(instance == null)
            instance = new ObjectCache();

        return instance;
    }

    @Override
    public void saveObject(Object key, Object value) {
        lruCache.put(key, value);
    }

    @Override
    public Object getValue(Object key) {
        return lruCache.get(key);
    }

    @Override
    public void clear() {
        lruCache.evictAll();
    }
}
