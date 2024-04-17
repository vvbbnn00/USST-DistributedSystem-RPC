package tech.bzpl.helloworld.server;

import cn.vvbbnn00.rpc.server.annotation.MethodPermission;
import cn.vvbbnn00.rpc.server.annotation.RpcExposed;

@RpcExposed
public class HelloWorld { // 服务端可以不实现接口
    public String sayHello() {
        return "Hello World!";
    }

    @MethodPermission(isPublic = true)
    private String sayHello(String name) {
        return "Hello " + name + "!";
    }

    private String youCantSeeMe() {
        return "You can't see me!";
    }

    @MethodPermission(isPublic = false)
    public String youCantSeeMeEither() {
        return "You can't see me either!";
    }
}
