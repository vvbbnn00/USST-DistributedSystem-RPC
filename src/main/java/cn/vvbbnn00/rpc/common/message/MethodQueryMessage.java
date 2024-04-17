package cn.vvbbnn00.rpc.common.message;

import cn.vvbbnn00.rpc.common.MethodDeclaration;

import java.beans.JavaBean;

@JavaBean
public class MethodQueryMessage extends Message {
    private MethodDeclaration method;

    public MethodQueryMessage(MethodDeclaration method) {
        this.method = method;
    }

    public MethodDeclaration getMethod() {
        return method;
    }

    public void setMethod(MethodDeclaration method) {
        this.method = method;
    }
}
