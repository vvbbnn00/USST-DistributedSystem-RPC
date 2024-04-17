package cn.vvbbnn00.rpc.common;

import java.beans.JavaBean;
import java.io.Serializable;
import java.util.Arrays;

@JavaBean
public class MethodDeclaration implements Serializable {
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;

    public MethodDeclaration(String className, String methodName, Class<?>[] parameterTypes) {
        this.className = className;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(className).append('.').append(methodName).append('(');
        for (Class<?> parameterType : parameterTypes) {
            sb.append(parameterType.getTypeName()).append(',');
        }
        sb.append(')');
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MethodDeclaration that = (MethodDeclaration) obj;
        return className.equals(that.className) && methodName.equals(that.methodName) && Arrays.equals(parameterTypes, that.parameterTypes);
    }

    @Override
    public int hashCode() {
        int result = className.hashCode();
        result = 31 * result + methodName.hashCode();
        result = 31 * result + Arrays.hashCode(parameterTypes);
        return result;
    }
}
