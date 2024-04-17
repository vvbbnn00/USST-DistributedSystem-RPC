package cn.vvbbnn00.rpc.server.base;
import cn.vvbbnn00.rpc.server.context.Context;

public class RpcHandler {
    private final Context sessionContext = new Context();
    private Context serverContext;

    public RpcHandler() {
        super();
    }

    public Context getSessionContext() {
        return sessionContext;
    }

    public Context getServerContext() {
        return serverContext;
    }

    public void setServerContext(Context serverContext) {
        this.serverContext = serverContext;
    }
}
