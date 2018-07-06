package org.fossasia.openevent.app.data;

import android.os.SystemClock;
import android.support.v4.util.ArrayMap;

import org.threeten.bp.Duration;

import java.util.Map;

/**
 * Utility class that decides whether we should fetch some data or not.
 */
public class RateLimiter<K> {
    private Map<K, Long> timestamps = new ArrayMap<>();
    private final long timeout;

    public RateLimiter(Duration duration) {
        this.timeout = duration.toMillis();
    }

    public synchronized boolean shouldFetch(K key) {
        Long lastFetched = timestamps.get(key);
        long now = now();
        if (lastFetched == null) {
            timestamps.put(key, now);
            return true;
        }
        if (now - lastFetched > timeout) {
            timestamps.put(key, now);
            return true;
        }
        return false;
    }

    private long now() {
        return SystemClock.uptimeMillis();
    }

    public synchronized void reset(K key) {
        timestamps.remove(key);
    }
}
