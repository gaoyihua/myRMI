package com.gary.test;

import com.gary.core.EServiceCommand;
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
        rpcServer.setServiceName(EServiceCommand.VIDEO_SERVICE);
        rpcServer.setIp("127.0.0.1");
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
