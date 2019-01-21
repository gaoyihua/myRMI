package com.gary.core;

import com.gary.util.CloseableUtil;
import com.gary.util.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.Map;

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
    <T> T rpcExecutor(String rpcBeanId, Object[] parameters, Class<?> returnType) throws  Exception {
        Socket socket = new Socket(rpcServerIp, rpcServerPort);
        Gson gson = (new GsonBuilder()).create();

//        GsonUtil gsonUtil = new GsonUtil();
////        gsonUtil.addArg(rpcBeanId, parameters);
////        String json = gsonUtil.toJson();

        System.out.println("rpcBeanId:" + gson.toJson(rpcBeanId));
        System.out.println("parameters:" + gson.toJson(parameters));
//        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeUTF(gson.toJson(rpcBeanId));
        dos.writeUTF(gson.toJson(parameters));
//        oos.writeUTF(rpcBeanId);
//        oos.writeObject(parameters);

//        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
//        Object result = ois.readObject();
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        String str = dis.readUTF();
        Object result = gson.fromJson(str, returnType);
        CloseableUtil.close(dos, dis, socket);
        return (T)result;
    }
}
