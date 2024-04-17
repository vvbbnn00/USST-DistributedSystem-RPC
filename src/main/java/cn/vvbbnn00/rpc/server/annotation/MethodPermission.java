package cn.vvbbnn00.rpc.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 显式指定方法是否为公开方法，如果不指定则默认检查该方法的声明，若声明为public则为公开方法，否则无法被RPC调用
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface MethodPermission {
    boolean isPublic();
}
