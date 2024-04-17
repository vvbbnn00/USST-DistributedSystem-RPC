package cn.vvbbnn00.rpc.registry.model;

import cn.vvbbnn00.rpc.common.MethodDeclaration;
import cn.vvbbnn00.rpc.registry.util.MethodHasher;

import java.util.*;

public class RegistryStorage {
    public static final Map<String, Set<ServerEndpoint>> METHOD_MAP = new HashMap<>();
    public static final Set<ServerEndpoint> SERVERS = new HashSet<>();
    public static final Map<ServerEndpoint, Set<String>> SERVER_METHOD_MAP = new HashMap<>();

    /**
     * 添加服务节点到注册中心
     *
     * @param server             服务节点
     * @param methodDeclarations 服务节点提供的方法
     * @return 服务节点的token
     */
    public synchronized static String addServer(ServerEndpoint server, Set<MethodDeclaration> methodDeclarations) {
        if (SERVERS.contains(server)) { // 服务节点已存在
            updateServer(server, methodDeclarations);
            return server.getToken();
        }
        server.setLastHeartbeat(System.currentTimeMillis());
        server.setToken(UUID.randomUUID().toString());
        SERVERS.add(server);
        SERVER_METHOD_MAP.put(server, new HashSet<>());
        for (MethodDeclaration methodDeclaration : methodDeclarations) {
            String key = MethodHasher.calcHash(
                    methodDeclaration.getClassName(),
                    methodDeclaration.getMethodName(),
                    methodDeclaration.getParameterTypes()
            );
            if (!METHOD_MAP.containsKey(key)) {
                METHOD_MAP.put(key, new HashSet<>());
            }
            METHOD_MAP.get(key).add(server);
            SERVER_METHOD_MAP.get(server).add(key);
        }

        return server.getToken();
    }

    /**
     * 从注册中心移除服务节点
     *
     * @param server 服务节点
     */
    public synchronized static void removeServer(ServerEndpoint server) {
        SERVERS.remove(server);
        for (String key : SERVER_METHOD_MAP.get(server)) {
            METHOD_MAP.get(key).remove(server);
        }
        SERVER_METHOD_MAP.remove(server);
    }

    /**
     * 更新服务节点提供的方法
     *
     * @param server             服务节点
     * @param methodDeclarations 服务节点提供的方法
     */
    public synchronized static void updateServer(ServerEndpoint server, Set<MethodDeclaration> methodDeclarations) {
        removeServer(server);
        addServer(server, methodDeclarations);
    }

    /**
     * 获取服务节点
     *
     * @param host 服务节点主机
     * @param port 服务节点端口
     * @return 服务节点
     */
    public synchronized static ServerEndpoint getServer(String host, int port) {
        for (ServerEndpoint server : SERVERS) {
            if (server.getHost().equals(host) && server.getPort() == port) {
                return server;
            }
        }
        return null;
    }

    public synchronized static Set<ServerEndpoint> getServers(String key) {
        return METHOD_MAP.get(key);
    }

    public synchronized static Set<ServerEndpoint> getAllServers() {
        return SERVERS;
    }

    /**
     * 心跳
     *
     * @param server 服务节点
     */
    public synchronized static void heartbeat(ServerEndpoint server) {
        server.setLastHeartbeat(System.currentTimeMillis());
    }

    /**
     * 查询方法
     *
     * @param methodDeclaration 方法声明
     * @return 服务节点列表
     */
    public synchronized static List<ServerEndpoint> queryMethod(MethodDeclaration methodDeclaration) {
        String key = MethodHasher.calcHash(
                methodDeclaration.getClassName(),
                methodDeclaration.getMethodName(),
                methodDeclaration.getParameterTypes()
        );
        Set<ServerEndpoint> servers = METHOD_MAP.get(key);
        if (servers == null || servers.isEmpty()) {
            return null;
        }

        return new ArrayList<>(servers);
    }
}
