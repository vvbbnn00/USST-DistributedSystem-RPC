package cn.vvbbnn00.rpc.common.message;

public class MethodQueryResponseMessage extends BasicResponseMessage {
    private String host;
    private int port;

    public MethodQueryResponseMessage(int code, String message) {
        super(code, message);
    }

    public MethodQueryResponseMessage(int code, String message, String host, int port) {
        super(code, message);
        this.host = host;
        this.port = port;
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
}
