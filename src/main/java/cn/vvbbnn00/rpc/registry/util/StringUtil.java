package cn.vvbbnn00.rpc.registry.util;

public class StringUtil {
    /**
     * 生成uuid
     * @return uuid
     */
    public static String uuid() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
}
