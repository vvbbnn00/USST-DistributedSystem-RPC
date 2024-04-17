package cn.vvbbnn00.rpc.common.message;

import java.beans.JavaBean;

@JavaBean
public class MethodResponseMessage extends BasicResponseMessage {
    private Object result;
    private Exception exception;

    public MethodResponseMessage(Object result, Exception exception) {
        super(0, "ok");
        this.result = result;
        this.exception = exception;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
