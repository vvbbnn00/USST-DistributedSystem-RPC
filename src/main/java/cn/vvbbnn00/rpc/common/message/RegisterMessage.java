package cn.vvbbnn00.rpc.common.message;

import cn.vvbbnn00.rpc.common.MethodDeclaration;

import java.beans.JavaBean;
import java.util.Set;

@JavaBean
public class RegisterMessage extends Message {
    private int port;
    private int weight;
    private Set<MethodDeclaration> methods;

    public RegisterMessage(int port, int weight, Set<MethodDeclaration> methods) {
        this.port = port;
        this.weight = weight;
        this.methods = methods;
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

    public Set<MethodDeclaration> getMethods() {
        return methods;
    }

    public void setMethods(Set<MethodDeclaration> methods) {
        this.methods = methods;
    }
}
