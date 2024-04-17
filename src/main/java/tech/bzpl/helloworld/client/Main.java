package tech.bzpl.helloworld.client;

import cn.vvbbnn00.rpc.client.RpcClient;
import tech.bzpl.helloworld.client.interfaces.IHelloWorld;

public class Main {
    public static void main(String[] args) {
        RpcClient client = new RpcClient("192.168.31.106", 10721);

        IHelloWorld proxy = (IHelloWorld) client.getInterface("helloworld.HelloWorld", IHelloWorld.class);
        System.out.println(proxy.sayHello());
        System.out.println(proxy.sayHello("World"));
    }
}
