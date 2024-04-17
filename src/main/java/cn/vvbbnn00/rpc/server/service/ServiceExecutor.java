package cn.vvbbnn00.rpc.server.service;

import cn.vvbbnn00.rpc.common.MethodDeclaration;
import cn.vvbbnn00.rpc.common.message.BasicResponseMessage;
import cn.vvbbnn00.rpc.common.message.MethodInvocationMessage;
import cn.vvbbnn00.rpc.common.message.MethodResponseMessage;
import cn.vvbbnn00.rpc.server.base.RpcHandler;
import cn.vvbbnn00.rpc.server.context.Context;
import cn.vvbbnn00.rpc.server.model.PackageNameMap;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.*;
import java.util.logging.Logger;

public class ServiceExecutor implements Runnable {
    private final Map<String, Object> instanceMap = new HashMap<>();
    private final Set<MethodDeclaration> methodDeclarations;
    private final Logger l = Logger.getLogger("ServiceExecutor");
    private final Socket socket;
    private final PackageNameMap packageNameMap;

    public ServiceExecutor(Set<MethodDeclaration> methodDeclarations, Socket socket, PackageNameMap packageNameMap, Context serverContext) {
        this.packageNameMap = packageNameMap;

        this.methodDeclarations = methodDeclarations;
        for (MethodDeclaration methodDeclaration : methodDeclarations) {
            String className = methodDeclaration.getClassName();
            className = packageNameMap.getRealClassName(className);

            if (instanceMap.containsKey(className)) {
                continue;
            }
            try {
                Class<?> clazz = Class.forName(className);
                Object instance = clazz.getDeclaredConstructor().newInstance();
                RpcHandler rpcHandler = (RpcHandler) instance;

                // 设置会话上下文
                rpcHandler.getSessionContext().setAttribute("remoteAddress", socket.getRemoteSocketAddress().toString());
                rpcHandler.setServerContext(serverContext);

                instanceMap.put(className, instance);
            } catch (Exception e) {
                l.severe("Failed to create instance for class " + methodDeclaration.getClassName());
                e.printStackTrace();
            }
        }
        this.socket = socket;
    }

    public MethodResponseMessage invoke(MethodInvocationMessage message) {
        MethodDeclaration methodDeclaration = message.getMethodDeclaration();
        Object[] args = message.getArgs();

        String className = methodDeclaration.getClassName();
        className = packageNameMap.getRealClassName(className);

        // 检查方法是否暴露
        if (!methodDeclarations.contains(methodDeclaration)) {
            l.warning("Method " + methodDeclaration + " is not exposed");
            return new MethodResponseMessage(null, new IllegalAccessException("Method " + methodDeclaration + " is not exposed"));
        }

        methodDeclaration.setClassName(className);

        // 调用方法
        try {
            Object instance = instanceMap.get(className);
            Method method = instance.getClass().getDeclaredMethod(methodDeclaration.getMethodName(), methodDeclaration.getParameterTypes());
            method.setAccessible(true);
            Object result = method.invoke(instance, args);
            return new MethodResponseMessage(result, null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            l.severe("Failed to invoke method " + methodDeclaration + ": " + e.getMessage());
            e.getCause().printStackTrace();
            // 追溯到上一层的异常，因为这里的异常是反射调用方法时抛出的异常
            return new MethodResponseMessage(null, (Exception) e.getCause());
        }
    }

    @Override
    public void run() {
        ObjectInputStream ois;
        ObjectOutputStream oos;
        Object message;

        // 读取客户端的请求
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            message = ois.readObject();
        } catch (Exception e) {
            l.severe("[ServiceExecutor] " + e.getMessage());
            try {
                socket.close();
            } catch (Exception ignored) {
            }
            return;
        }

        // 处理客户端的请求
        try {
            if (message instanceof MethodInvocationMessage) {
                oos.writeObject(invoke((MethodInvocationMessage) message));
            } else {
                oos.writeObject(new BasicResponseMessage(400, "Bad Request"));
            }
        } catch (Exception e) {
            l.severe("[RegistryService] " + e.getMessage());
            try {
                oos.writeObject(new BasicResponseMessage(500, e.getMessage()));
            } catch (Exception ignored) {
            }
        }

        try {
            socket.close();
        } catch (Exception ignored) {
        }
    }
}
