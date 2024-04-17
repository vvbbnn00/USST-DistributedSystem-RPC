package cn.vvbbnn00.rpc.registry;

import cn.vvbbnn00.rpc.registry.service.RegistryCron;
import cn.vvbbnn00.rpc.registry.service.RegistryService;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class Registry {
    private final String HOST;
    private final int PORT;
    private final int backlog;

    private final Logger l = Logger.getLogger("Registry");


    public Registry(String host, int port) {
        this(host, port, 50);
    }

    public Registry(String host, int port, int backlog) {
        this.HOST = host;
        this.PORT = port;
        this.backlog = backlog;
    }

    public void run() {
        // Start the cron
        new Thread(new RegistryCron()).start();

        InetAddress addr;
        try {
            addr = InetAddress.getByName(HOST);
        } catch (IOException e) {
            l.severe(e.getMessage());
            return;
        }

        // Socket server
        try (ServerSocket serverSocket = new ServerSocket(PORT, backlog, addr)) {
            l.info("[Main] Registry server started on " + HOST + ":" + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new RegistryService(socket)).start();
            }
        } catch (IOException e) {
            Logger.getGlobal().severe(e.getMessage());
        }
    }

    public static void main(String[] args) {
        String host = "0.0.0.0";
        int port = 10721;

        if (args.length > 2 || args.length == 1) {
            System.out.println("Usage: java -jar Registry.jar [host] [port]");
            return;
        }

        if (args.length == 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }
        new Registry(host, port).run();
    }
}
