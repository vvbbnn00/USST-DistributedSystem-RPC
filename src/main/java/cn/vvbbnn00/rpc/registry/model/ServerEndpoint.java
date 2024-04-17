package cn.vvbbnn00.rpc.registry.model;

import java.beans.JavaBean;
import java.util.Objects;

@JavaBean
public class ServerEndpoint {
    private String host;
    private int port;
    private long lastHeartbeat;
    private int weight;

    private String token;

    public ServerEndpoint(String host, int port) {
        this.host = host;
        this.port = port;
        this.weight = 1;
    }

    public ServerEndpoint(String host, int port, int weight) {
        this.host = host;
        this.port = port;
        this.weight = weight;
        if (weight < 1) {
            throw new IllegalArgumentException("Weight must be greater than 0");
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(long lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerEndpoint that = (ServerEndpoint) o;
        return port == that.port && Objects.equals(host, that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }

    @Override
    public String toString() {
        return "ServerEndpoint{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
