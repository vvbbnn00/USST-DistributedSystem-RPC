package cn.vvbbnn00.rpc.client.service;

import cn.vvbbnn00.rpc.client.model.ServerEndpoint;
import cn.vvbbnn00.rpc.client.util.LRUCacheWithTTL;
import cn.vvbbnn00.rpc.common.MethodDeclaration;
import cn.vvbbnn00.rpc.common.message.MethodQueryMessage;
import cn.vvbbnn00.rpc.common.message.MethodQueryResponseMessage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

public class RegistryClient {
    private final String host;
    private final int port;
    private final LRUCacheWithTTL<MethodDeclaration, ServerEndpoint> cache = new LRUCacheWithTTL<>(100, 60000);
    private final Logger l = Logger.getLogger("RegistryClient");

    public RegistryClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private ServerEndpoint getServerEndpoint(MethodDeclaration method) {
        try (Socket socket = new Socket(host, port)) {
            // Send the method query message
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(new MethodQueryMessage(method));
            oos.flush();
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            MethodQueryResponseMessage response = (MethodQueryResponseMessage) ois.readObject();
            if (response.getCode() != 0) {
                l.severe(response.getMessage());
                return null;
            }
            // l.info("Found server endpoint: " + endpoint);
            return new ServerEndpoint(response.getHost(), response.getPort());
        } catch (Exception e) {
            l.severe(e.getMessage());
            return null;
        }
    }

    public ServerEndpoint findService(MethodDeclaration method) {
        ServerEndpoint endpoint = cache.get(method);
        if (endpoint != null) {
            return endpoint;
        }
        endpoint = getServerEndpoint(method);
        if (endpoint != null) {
            cache.put(method, endpoint);
        }
        return endpoint;
    }

    public ServerEndpoint forceFindService(MethodDeclaration method) {
        ServerEndpoint endpoint = getServerEndpoint(method);
        if (endpoint != null) {
            cache.put(method, endpoint);
        }
        return endpoint;
    }
}
