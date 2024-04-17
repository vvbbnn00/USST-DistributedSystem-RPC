package cn.vvbbnn00.rpc.server.model;

import java.beans.JavaBean;
import java.util.List;

@JavaBean
public class ConfigurationFile {
    public static String CONFIG_FILE = "rpc_server.yaml";
    private ServerConfiguration server;
    private RegistryConfiguration registry;
    private List<ServicePackage> servicePackages;

    public ConfigurationFile() {
    }

    public ConfigurationFile(ServerConfiguration server, RegistryConfiguration registry) {
        this.server = server;
        this.registry = registry;
    }

    public List<ServicePackage> getServicePackages() {
        return servicePackages;
    }

    public void setServicePackages(List<ServicePackage> servicePackages) {
        this.servicePackages = servicePackages;
    }

    public ServerConfiguration getServer() {
        return server;
    }

    public void setServer(ServerConfiguration server) {
        this.server = server;
    }

    public RegistryConfiguration getRegistry() {
        return registry;
    }

    public void setRegistry(RegistryConfiguration registry) {
        this.registry = registry;
    }
}
