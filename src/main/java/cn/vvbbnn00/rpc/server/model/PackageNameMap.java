package cn.vvbbnn00.rpc.server.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PackageNameMap {
    public final Map<String, String> PACKAGE_NAME_MAP = new HashMap<>();

    public synchronized void put(String key, String value) {
        PACKAGE_NAME_MAP.put(key, value);
    }

    public synchronized String get(String key) {
        return PACKAGE_NAME_MAP.get(key);
    }

    public synchronized void clear() {
        PACKAGE_NAME_MAP.clear();
    }

    public synchronized int size() {
        return PACKAGE_NAME_MAP.size();
    }

    public synchronized Set<String> keySet() {
        return PACKAGE_NAME_MAP.keySet();
    }

    public synchronized String getRealClassName(String className) {
        String longestMatch = null;
        int maxLength = 0;

        // 将包名映射为真实的包名，最长匹配
        for (String packageName : PACKAGE_NAME_MAP.keySet()) {
            if (className.startsWith(packageName) && packageName.length() > maxLength) {
                maxLength = packageName.length();
                longestMatch = packageName;
            }
        }

        if (longestMatch != null) {
            return PACKAGE_NAME_MAP.get(longestMatch) + className.substring(longestMatch.length());
        }

        return className;
    }
}
