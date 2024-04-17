package cn.vvbbnn00.rpc.client;

import cn.vvbbnn00.rpc.client.model.ServerEndpoint;
import cn.vvbbnn00.rpc.client.service.RegistryClient;
import cn.vvbbnn00.rpc.client.service.ServerClient;
import cn.vvbbnn00.rpc.common.MethodDeclaration;
import cn.vvbbnn00.rpc.common.message.MethodResponseMessage;

import java.lang.reflect.Proxy;
import java.util.logging.Logger;

public class RpcClient {
    private final Logger l = Logger.getLogger("RpcClient");
    private final RegistryClient registryClient;

    public RpcClient(String host, int port) {
        this.registryClient = new RegistryClient(host, port);
    }

    public Object getInterface(String className, Class<?>... interfaces) {
        return Proxy.newProxyInstance(
                RpcClient.class.getClassLoader(),
                interfaces,
                (proxy1, method, args) -> {
                    String methodName = method.getName();
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    MethodDeclaration methodDeclaration = new MethodDeclaration(className, methodName, parameterTypes);

                    ServerEndpoint endpoint = registryClient.findService(methodDeclaration);
                    if (endpoint == null) {
                        throw new RuntimeException("No available server for method " + methodDeclaration);
                    }

                    // 如果缓存的服务端点不可用，强制查找
                    int retry = 3;
                    MethodResponseMessage mrm = null;
                    while (retry-- > 0) {
                        try {
                            mrm = ServerClient.remoteCall(endpoint, methodDeclaration, args);
                        } catch (Exception e) {
                            l.severe("Connection to cached server failed: " + e.getMessage());
                            endpoint = registryClient.forceFindService(methodDeclaration);
                            if (endpoint == null) {
                                throw new RuntimeException("No available server for method " + methodDeclaration);
                            }
                        }
                    }
                    if (mrm == null) {
                        throw new RuntimeException("No available server for method " + methodDeclaration);
                    }

                    if (mrm.getException() != null) {
                        throw mrm.getException();
                    }
                    return mrm.getResult();
                }
        );
    }
}
