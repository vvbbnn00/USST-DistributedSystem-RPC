package cn.vvbbnn00.rpc.server.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Context {
    private final Map<String, Object> ATTRIBUTES;

    public Context() {
        ATTRIBUTES = new ConcurrentHashMap<>();
    }

    public synchronized void setAttribute(String key, Object value) {
        ATTRIBUTES.put(key, value);
    }

    public synchronized Object getAttribute(String key) {
        return ATTRIBUTES.get(key);
    }

    public synchronized void removeAttribute(String key) {
        ATTRIBUTES.remove(key);
    }

    public synchronized boolean containsAttribute(String key) {
        return ATTRIBUTES.containsKey(key);
    }

    public synchronized void clear() {
        ATTRIBUTES.clear();
    }
}
