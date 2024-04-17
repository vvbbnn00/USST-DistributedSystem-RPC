package tech.bzpl.ecard.client;

import cn.vvbbnn00.rpc.client.RpcClient;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

public class CialloClient {
    private static final int PORT = 10721;
    private static final String HOST = "192.168.31.106";

    public static void main(String[] args) {
        RpcClient client = new RpcClient(HOST, PORT);
        ICiallo ciallo = (ICiallo) client.getInterface("ecard.CialloServer", ICiallo.class);

        new CialloClientTerminal(ciallo).run();
    }
}
