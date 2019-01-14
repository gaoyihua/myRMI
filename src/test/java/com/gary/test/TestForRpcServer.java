package com.gary.test;

import com.gary.action.UserAction;
import com.gary.action.impl.UserActionImpl;
import com.gary.core.RpcServer;

/**
 * describe:
 *
 * @author gary
 * @date 2019/01/12
 */
public class TestForRpcServer {
    public static void main(String[] args) {
        RpcServer rpcServer = new RpcServer();
        rpcServer.setPort(54189);
//        rpcServer.registryRpc(UserAction.class, UserActionImpl.class);
//        rpcServer.registryRpc(UserActionImpl.class);
        rpcServer.registryRpc("/rpcConfig.xml");
        try {
            rpcServer.startRpcServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
