package cn.vvbbnn00.rpc.registry.service;

import cn.vvbbnn00.rpc.registry.model.RegistryStorage;
import cn.vvbbnn00.rpc.registry.model.ServerEndpoint;

import java.util.Set;
import java.util.logging.Logger;

public class RegistryCron implements Runnable {
    private static final int CHECK_INTERVAL = 2000;
    private static final int MAX_TIMEOUT = 10000;
    private final Logger l = Logger.getLogger("RegistryCron");

    private void checkServer() {
        // l.info("[RegistryCron] Checking server...");
        Set<ServerEndpoint> serverEndpointList = RegistryStorage.getAllServers();

        for (ServerEndpoint server : serverEndpointList) {
            if (System.currentTimeMillis() - server.getLastHeartbeat() > MAX_TIMEOUT) {
                l.info("[RegistryCron] Server " + server.getHost() + ":" + server.getPort() + " is offline");
                RegistryStorage.removeServer(server);
            }
        }

        // l.info("[RegistryCron] Checking server done");
    }

    @Override
    public void run() {
        while (true) {
            try {
                checkServer();
                Thread.sleep(CHECK_INTERVAL);
            } catch (InterruptedException e) {
                l.severe(e.getMessage());
            }
        }
    }
}
