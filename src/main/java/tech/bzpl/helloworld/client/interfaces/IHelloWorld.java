package tech.bzpl.helloworld.client.interfaces;

// 客户端在调用之前，需要知道服务端的接口定义
public interface IHelloWorld {
    String sayHello();
    String sayHello(String name);
    String youCantSeeMe();
    String youCantSeeMeEither();
}
