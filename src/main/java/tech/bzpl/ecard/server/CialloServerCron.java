package tech.bzpl.ecard.server;
import cn.vvbbnn00.rpc.server.base.RpcRunner;
import cn.vvbbnn00.rpc.server.context.Context;

import java.util.logging.Logger;


// Check the clients every 10 seconds and remove any that have been disconnected
public class CialloServerCron extends RpcRunner {
    private final Logger l = Logger.getLogger("CialloServerCron");

    @Override
    public void run() {
        Context serverContext = getServerContext();

        while (true) {
            try {
                l.info("[CialloServerCron] Commit changes into fs...");
                CialloCardManager manager = (CialloCardManager) serverContext.getAttribute("manager");
                if (manager == null) {
                    manager = new CialloCardManager();
                    serverContext.setAttribute("manager", manager);
                }
                manager.saveCardData();
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                l.severe(e.getMessage());
            }
        }
    }
}