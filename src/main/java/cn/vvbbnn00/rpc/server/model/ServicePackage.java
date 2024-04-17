package cn.vvbbnn00.rpc.server.model;

import java.beans.JavaBean;
import java.util.List;

@JavaBean
public class ServicePackage {
    private String packageName;
    private List<String> alias;
    private boolean registerPackageName = true; // 是否在注册别名的同时，将完整包名注册

    public ServicePackage() {
    }

    public ServicePackage(String packageName, List<String> alias) {
        this.packageName = packageName;
        this.alias = alias;
    }

    public boolean isRegisterPackageName() {
        return registerPackageName;
    }

    public void setRegisterPackageName(boolean registerPackageName) {
        this.registerPackageName = registerPackageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<String> getAlias() {
        return alias;
    }

    public void setAlias(List<String> alias) {
        this.alias = alias;
    }
}
