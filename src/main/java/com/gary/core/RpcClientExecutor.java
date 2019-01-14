package com.gary.core;

import com.gary.util.CloseableUtil;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * describe:RPC客户端执行者
 *
 * @author gary
 * @date 2019/01/12
 */
public class RpcClientExecutor {
    private String rpcServerIp;
    private int rpcServerPort;

    public RpcClientExecutor() {
    }

    public RpcClientExecutor(String rpcServerIp, int rpcServerPort) {
        this.rpcServerIp = rpcServerIp;
        this.rpcServerPort = rpcServerPort;
    }

    public String getRpcServerIp() {
        return rpcServerIp;
    }

    public void setRpcServerIp(String rpcServerIp) {
        this.rpcServerIp = rpcServerIp;
    }

    public int getRpcServerPort() {
        return rpcServerPort;
    }

    public void setRpcServerPort(int rpcServerPort) {
        this.rpcServerPort = rpcServerPort;
    }

    @SuppressWarnings("unchecked")
    <T> T rpcExecutor(String rpcBeanId, Object[] parameters) throws  Exception {
        Socket socket = new Socket(rpcServerIp, rpcServerPort);

        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeUTF(rpcBeanId);
        oos.writeObject(parameters);

        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        Object result = ois.readObject();

        CloseableUtil.close(ois, oos, socket);

        return (T)result;
    }
}
