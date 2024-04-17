package cn.vvbbnn00.rpc.registry.util;

import java.lang.reflect.Type;
import java.security.MessageDigest;

public class MethodHasher {
    /**
     * 计算方法签名的哈希值
     *
     * @param className      类名
     * @param methodName     方法名
     * @param parameterTypes 参数类型
     * @return 哈希值
     */
    public static String calcHash(String className, String methodName, Type[] parameterTypes) {
        StringBuilder sb = new StringBuilder();
        sb.append(className).append('.').append(methodName).append('(');
        for (Type parameterType : parameterTypes) {
            sb.append(parameterType.getTypeName()).append(',');
        }
        sb.append(')');
        // 拼接后的字符串形如 "cn.vvbbnn00.util.MethodHasher.calcHash(java.lang.String,java.lang.String,)"
        // 计算该字符串的哈希值并返回，考虑到性能和唯一性保证，使用 SHA-1 算法
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] bytes = md.digest(sb.toString().getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : bytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            // 如果 SHA-1 算法不可用，使用默认的 hashCode() 方法
            return Integer.toHexString(sb.toString().hashCode());
        }
    }
}
