package cn.vvbbnn00.rpc.registry.service;

import cn.vvbbnn00.rpc.common.MethodDeclaration;
import cn.vvbbnn00.rpc.common.message.*;
import cn.vvbbnn00.rpc.registry.model.RegistryStorage;
import cn.vvbbnn00.rpc.registry.model.ServerEndpoint;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class RegistryService implements Runnable {
    private final Logger l = Logger.getLogger("RegistryService");
    private final Socket socket;

    public RegistryService(Socket socket) {
        this.socket = socket;
    }

    public RegisterResponseMessage register(RegisterMessage message) {
        String host = socket.getInetAddress().getHostAddress();
        ServerEndpoint endpoint = new ServerEndpoint(host, message.getPort(), message.getWeight());
        Set<MethodDeclaration> methods = message.getMethods();

        String token = RegistryStorage.addServer(endpoint, methods);
        l.info("[RegistryService] Registered server " + host + ":" + message.getPort() + " with token " + token);
        return new RegisterResponseMessage(0, "ok", token, endpoint.getHost());
    }

    public BasicResponseMessage heartbeat(HeartbeatMessage message) {
        String host = socket.getInetAddress().getHostAddress();
        ServerEndpoint server = RegistryStorage.getServer(host, message.getPort());
        if (server == null) {
            return new BasicResponseMessage(404, "Server not found");
        }
        if (!server.getToken().equals(message.getToken())) {
            return new BasicResponseMessage(401, "Unauthorized");
        }
        RegistryStorage.heartbeat(server);
        return new BasicResponseMessage(0, "ok");
    }

    public MethodQueryResponseMessage queryMethod(MethodQueryMessage message) {
        // System.out.println(RegistryStorage.METHOD_MAP);
        List<ServerEndpoint> servers = RegistryStorage.queryMethod(message.getMethod());
        if (servers == null || servers.isEmpty()) {
            return new MethodQueryResponseMessage(404, "Method not found");
        }

        // 根据负载均衡策略，首先读取 ServerEndpoint 的权重
        // 然后根据权重随机选择一个 ServerEndpoint
        int totalWeight = servers.stream().mapToInt(ServerEndpoint::getWeight).sum();
        int random = (int) (Math.random() * totalWeight);
        MethodQueryResponseMessage response = new MethodQueryResponseMessage(0, "ok");

        for (ServerEndpoint server : servers) {
            random -= server.getWeight();
            if (random < 0) {
                response.setHost(server.getHost());
                response.setPort(server.getPort());
                return response;
            }
        }

        // 如果没有选择到 ServerEndpoint，返回第一个
        ServerEndpoint server = servers.get(0);
        response.setHost(server.getHost());
        response.setPort(server.getPort());

        return response;
    }

    @Override
    public void run() {
        ObjectInputStream ois;
        ObjectOutputStream oos;
        Object message;

        // 读取客户端的请求
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            message = ois.readObject();
        } catch (Exception e) {
            l.severe("[RegistryService] " + e.getMessage());
            e.printStackTrace();
            try {
                socket.close();
            } catch (Exception ignored) {
            }
            return;
        }

        // 处理客户端的请求
        try {
            if (message instanceof RegisterMessage) {
                oos.writeObject(register((RegisterMessage) message));
            } else if (message instanceof HeartbeatMessage) {
                oos.writeObject(heartbeat((HeartbeatMessage) message));
            } else if (message instanceof MethodQueryMessage) {
                oos.writeObject(queryMethod((MethodQueryMessage) message));
            } else {
                oos.writeObject(new BasicResponseMessage(400, "Bad Request"));
            }
        } catch (Exception e) {
            l.severe("[RegistryService] " + e.getMessage());
            e.printStackTrace();
            try {
                oos.writeObject(new BasicResponseMessage(500, e.getMessage()));
            } catch (Exception ignored) {
            }
        }

        try {
            socket.close();
        } catch (Exception ignored) {
        }
    }
}
