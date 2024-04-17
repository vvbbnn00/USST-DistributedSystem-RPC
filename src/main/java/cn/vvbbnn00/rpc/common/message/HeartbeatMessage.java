package cn.vvbbnn00.rpc.common.message;

import java.beans.JavaBean;

@JavaBean
public class HeartbeatMessage extends Message {
    private int port;
    private String token;

    public HeartbeatMessage() {
    }

    public HeartbeatMessage(int port, String token) {
        this.port = port;
        this.token = token;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
