package cn.vvbbnn00.rpc.common.message;

import cn.vvbbnn00.rpc.common.MethodDeclaration;

import java.beans.JavaBean;

@JavaBean
public class MethodInvocationMessage extends Message {
    private MethodDeclaration methodDeclaration;
    private Object[] args;

    public MethodInvocationMessage(MethodDeclaration methodDeclaration, Object[] args) {
        this.methodDeclaration = methodDeclaration;
        this.args = args;
    }

    public MethodDeclaration getMethodDeclaration() {
        return methodDeclaration;
    }

    public void setMethodDeclaration(MethodDeclaration methodDeclaration) {
        this.methodDeclaration = methodDeclaration;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
