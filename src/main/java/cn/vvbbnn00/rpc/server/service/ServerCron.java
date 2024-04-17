package cn.vvbbnn00.rpc.server.service;

import cn.vvbbnn00.rpc.common.MethodDeclaration;
import cn.vvbbnn00.rpc.common.message.*;
import cn.vvbbnn00.rpc.server.model.RegistryConfiguration;
import cn.vvbbnn00.rpc.server.model.ServerConfiguration;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Set;
import java.util.logging.Logger;

public class ServerCron implements Runnable {
    private static final int HEARTBEAT_INTERVAL = 2000;
    private static final int RECONNECT_INTERVAL = 5000;
    private final Logger l = Logger.getLogger("ServerCron");
    private final ServerConfiguration serverConfiguration;
    private final RegistryConfiguration registryConfiguration;
    private final Set<MethodDeclaration> methods;
    private boolean disconnected = true;

    public ServerCron(ServerConfiguration serverConfiguration, RegistryConfiguration registryConfiguration, Set<MethodDeclaration> methods) {
        this.serverConfiguration = serverConfiguration;
        this.registryConfiguration = registryConfiguration;
        this.methods = methods;
    }

    private Socket connectToRegistry() throws IOException {
        return new Socket(registryConfiguration.getHost(), registryConfiguration.getPort());
    }

    public void sendHeartbeat() {
        ObjectOutputStream oos;
        ObjectInputStream ois;
        try (Socket socket = connectToRegistry()) {
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(new HeartbeatMessage(
                    serverConfiguration.getPort(),
                    serverConfiguration.getToken()
            ));
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            Message response = (Message) ois.readObject();
            if (response instanceof BasicResponseMessage brm) {
                if (brm.getCode() != 0) {
                    l.severe("Failed to send heartbeat: " + brm.getMessage());
                    disconnected = true;
                }
            } else {
                l.severe("Invalid response received: " + response);
                disconnected = true;
            }
        } catch (Exception e) {
            l.severe("Failed to send heartbeat: " + e.getMessage());
            disconnected = true;
        }
    }

    public void register() {
        l.info("Trying to register to registry server");
        try (Socket socket = connectToRegistry()) {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            RegisterMessage message = new RegisterMessage(
                    serverConfiguration.getPort(),
                    serverConfiguration.getWeight(),
                    methods
            );
            oos.writeObject(message);
            oos.flush();
            RegisterResponseMessage response = (RegisterResponseMessage) ois.readObject();
            if (response.getCode() != 0) {
                disconnected = true;
                l.warning("Failed to register to registry server: " + response.getMessage());
                return;
            }

            serverConfiguration.setToken(response.getToken());
            serverConfiguration.setHost(response.getHost());
            l.info("Registered, new host: " + serverConfiguration.getHost() + ", token: " + serverConfiguration.getToken());
            disconnected = false;
        } catch (Exception e) {
            l.severe("Failed to register to registry server: " + e.getMessage());
            disconnected = true;
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (disconnected) {
                    register();
                    if (disconnected) {
                        l.warning("Failed to register, retrying in " + RECONNECT_INTERVAL + "ms");
                        Thread.sleep(RECONNECT_INTERVAL);
                    }
                } else {
                    sendHeartbeat();
                    Thread.sleep(HEARTBEAT_INTERVAL);
                }
            } catch (InterruptedException e) {
                l.severe(e.getMessage());
            }
        }
    }
}
