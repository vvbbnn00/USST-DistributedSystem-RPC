package cn.vvbbnn00.rpc.server.model;

import java.beans.JavaBean;

@JavaBean
public class ServerConfiguration {
    private String host;
    private int port;
    private int weight;
    private String token;
    private int backlog = 50;

    public int getScanDepth() {
        return scanDepth;
    }

    public void setScanDepth(int scanDepth) {
        this.scanDepth = scanDepth;
    }

    private int scanDepth = 5;

    public ServerConfiguration() {
    }

    public ServerConfiguration(String host, int port) {
        this(host, port, 1, null);
    }

    public ServerConfiguration(String host, int port, int weight) {
        this(host, port, weight, null);
    }

    public ServerConfiguration(String host, int port, int weight, String token) {
        this.host = host;
        this.port = port;
        this.weight = weight;
        this.token = token;
        if (weight < 1) {
            throw new IllegalArgumentException("Weight must be greater than 0");
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }
}
