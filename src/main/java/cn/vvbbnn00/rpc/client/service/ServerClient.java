package cn.vvbbnn00.rpc.client.service;

import cn.vvbbnn00.rpc.common.MethodDeclaration;
import cn.vvbbnn00.rpc.common.message.MethodInvocationMessage;
import cn.vvbbnn00.rpc.common.message.MethodResponseMessage;
import cn.vvbbnn00.rpc.client.model.ServerEndpoint;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

public class ServerClient {
    private static final Logger l = Logger.getLogger("ServerClient");

    public static Object remoteCall(ServerEndpoint endpoint, MethodDeclaration method, Object[] args) {
        try (Socket socket = new Socket(endpoint.getHost(), endpoint.getPort())) {
            // Send the method call message
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(new MethodInvocationMessage(method, args));
            oos.flush();
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            MethodResponseMessage response = (MethodResponseMessage) ois.readObject();
            if (response.getException() != null) {
                throw response.getException();
            }
            return response.getResult();
        } catch (Exception e) {
            l.severe(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
