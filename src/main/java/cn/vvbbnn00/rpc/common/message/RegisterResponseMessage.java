package cn.vvbbnn00.rpc.common.message;

import java.beans.JavaBean;

@JavaBean
public class RegisterResponseMessage extends BasicResponseMessage {
    private String token;
    private String host;

    public RegisterResponseMessage(int code, String message, String token, String host) {
        super(code, message);
        this.token = token;
        this.host = host;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
