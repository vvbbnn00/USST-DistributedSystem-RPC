package cn.vvbbnn00.rpc.server;

import cn.vvbbnn00.rpc.common.MethodDeclaration;
import cn.vvbbnn00.rpc.server.model.ConfigurationFile;
import cn.vvbbnn00.rpc.server.model.PackageNameMap;
import cn.vvbbnn00.rpc.server.model.ServerConfiguration;
import cn.vvbbnn00.rpc.server.model.ServicePackage;
import cn.vvbbnn00.rpc.server.service.ClassScanner;
import cn.vvbbnn00.rpc.server.service.ServerCron;
import cn.vvbbnn00.rpc.server.service.ServiceExecutor;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class Server {
    private String configPath = ConfigurationFile.CONFIG_FILE;
    private ConfigurationFile configurationFile;
    private Set<MethodDeclaration> methods;
    private PackageNameMap packageNameMap;
    private final Logger l = Logger.getLogger("Server");

    public Server() {
    }

    public Server(String configPath) {
        this.configPath = configPath;
    }

    public void loadConfiguration() {
        try {
            LoaderOptions options = new LoaderOptions();
            Yaml yaml = new Yaml(new Constructor(ConfigurationFile.class, options));
            InputStream inputStream = ConfigurationFile.class
                    .getClassLoader()
                    .getResourceAsStream(configPath);
            configurationFile = yaml.load(inputStream);
        } catch (Exception e) {
            l.severe("Failed to load configuration file: " + e.getMessage());
            System.exit(1);
        }
    }

    public void loadMethods() {
        if (methods == null) {
            methods = new HashSet<>();
        }
        methods.clear();
        for (ServicePackage sp : configurationFile.getServicePackages()) {
            methods.addAll(
                    new ClassScanner(
                            sp.getPackageName(),
                            configurationFile.getServicePackages(),
                            configurationFile.getServer().getScanDepth()
                    ).getMethodDeclarations()
            );
        }
        l.info("Loaded " + methods.size() + " methods from " + configurationFile.getServicePackages().size() + " packages");
    }

    public void loadPackageNameMap() {
        if (packageNameMap == null) {
            packageNameMap = new PackageNameMap();
        }
        packageNameMap.clear();
        for (ServicePackage sp : configurationFile.getServicePackages()) {
            for (String alia : sp.getAlias()) {
                packageNameMap.put(alia, sp.getPackageName());
            }
        }

        l.info("Loaded " + packageNameMap.size() + " package aliases");
    }


    public void startListener() {
        // Start the cron
        new Thread(new ServerCron(
                configurationFile.getServer(),
                configurationFile.getRegistry(),
                methods
        )).start();

        InetAddress addr;
        ServerConfiguration serverConfiguration = configurationFile.getServer();
        try {
            addr = InetAddress.getByName(serverConfiguration.getHost());
        } catch (IOException e) {
            l.severe(e.getMessage());
            return;
        }

        // Socket server
        try (ServerSocket serverSocket = new ServerSocket(serverConfiguration.getPort(), serverConfiguration.getBacklog(), addr)) {
            l.info("RPC server started on " + serverConfiguration.getHost() + ":" + serverConfiguration.getPort());
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ServiceExecutor(methods, socket, packageNameMap)).start();
            }
        } catch (IOException e) {
            Logger.getGlobal().severe(e.getMessage());
        }
    }

    public void start() {
        loadConfiguration();
        loadPackageNameMap();
        loadMethods();
        startListener();
    }

    public static void main(String[] args) {
        if (args.length > 1) {
            System.out.printf("Usage: java %s [config file]\n", Server.class.getName());
            return;
        }
        String configPath = ConfigurationFile.CONFIG_FILE;
        if (args.length == 1) {
            configPath = args[0];
        }
        new Server(configPath).start();
    }
}
