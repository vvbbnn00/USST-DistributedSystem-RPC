package cn.vvbbnn00.rpc.common.message;

import java.beans.JavaBean;

@JavaBean
public class BasicResponseMessage extends Message{
    private int code;
    private String message;

    public BasicResponseMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
