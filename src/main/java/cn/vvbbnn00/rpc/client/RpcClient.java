package cn.vvbbnn00.rpc.client;

import cn.vvbbnn00.rpc.client.model.ServerEndpoint;
import cn.vvbbnn00.rpc.client.service.RegistryClient;
import cn.vvbbnn00.rpc.client.service.ServerClient;
import cn.vvbbnn00.rpc.common.MethodDeclaration;

import java.lang.reflect.Proxy;
import java.util.logging.Logger;

public class RpcClient {
    private final Logger l = Logger.getLogger("RpcClient");
    private final RegistryClient registryClient;

    public RpcClient(String host, int port) {
        this.registryClient = new RegistryClient(host, port);
    }

    public Object getInterface(String className, Class<?>[] interfaces) {
        return Proxy.newProxyInstance(
                RpcClient.class.getClassLoader(),
                interfaces,
                (proxy1, method, args) -> {
                    String methodName = method.getName();
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    MethodDeclaration methodDeclaration = new MethodDeclaration(className, methodName, parameterTypes);
                    // l.info("[RpcClient] Method declaration: " + methodDeclaration);
                    ServerEndpoint endpoint = registryClient.findService(methodDeclaration);
                    // l.info("[RpcClient] Found server endpoint: " + endpoint);

                    if (endpoint == null) {
                        throw new RuntimeException("No available server for method " + methodDeclaration);
                    }

                    return ServerClient.remoteCall(endpoint, methodDeclaration, args);
                }
        );
    }
}
