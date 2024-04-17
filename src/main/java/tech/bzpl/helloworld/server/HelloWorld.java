package tech.bzpl.helloworld.server;

import cn.vvbbnn00.rpc.server.annotation.MethodPermission;
import cn.vvbbnn00.rpc.server.annotation.RpcExposed;
import cn.vvbbnn00.rpc.server.base.RpcHandler;

@RpcExposed
public class HelloWorld extends RpcHandler { // 服务端可以不实现接口，但是必须继承RpcHandler
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
