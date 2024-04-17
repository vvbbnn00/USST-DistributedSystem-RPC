package cn.vvbbnn00.rpc.client.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCacheWithTTL<K, V> {
    private final LinkedHashMap<K, CacheEntry<V>> cacheMap;
    private final int capacity;
    private final long ttl;

    public LRUCacheWithTTL(int capacity, long ttl) {
        this.capacity = capacity;
        this.ttl = ttl;
        this.cacheMap = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheEntry<V>> eldest) {
                return size() > LRUCacheWithTTL.this.capacity || isExpired(eldest.getValue().timestamp);
            }
        };
    }

    public void put(K key, V value) {
        cacheMap.put(key, new CacheEntry<>(value, System.currentTimeMillis()));
    }

    public V get(K key) {
        CacheEntry<V> entry = cacheMap.get(key);
        if (entry != null && !isExpired(entry.timestamp)) {
            return entry.value;
        }
        return null; // Return null if entry is not found or it is expired.
    }

    private boolean isExpired(long timestamp) {
        return (System.currentTimeMillis() - timestamp) > ttl;
    }

    private static class CacheEntry<V> {
        V value;
        long timestamp;

        CacheEntry(V value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
    }
}
