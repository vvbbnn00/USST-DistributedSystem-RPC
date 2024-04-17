package cn.vvbbnn00.rpc.server.base;

import cn.vvbbnn00.rpc.server.context.Context;

public class RpcRunner implements Runnable {
    private Context serverContext;

    public Context getServerContext() {
        return serverContext;
    }

    public void setServerContext(Context serverContext) {
        this.serverContext = serverContext;
    }

    @Override
    public void run() {
    }
}
