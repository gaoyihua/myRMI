package com.gary.core;

import com.gary.util.CloseableUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * describe:RPC服务器端执行者
 *
 * @author gary
 * @date 2019/01/12
 */
public class RpcServerExecutor implements Runnable {
    private Socket socket;
    private RpcServer rpcServer;
    private ThreadPoolExecutor threadPool;
    private DataInputStream dis;
    private DataOutputStream dos;

    public RpcServerExecutor(Socket socket, RpcServer rpcServer, ThreadPoolExecutor threadPool, long threadId) throws IOException {
        this.socket = socket;
        this.rpcServer = rpcServer;
        this.dis = new DataInputStream(this.socket.getInputStream());
        this.dos = new DataOutputStream(this.socket.getOutputStream());
        this.threadPool = threadPool;
        this.threadPool.execute(this);
    }

    @Override
    public void run() {
        try {
            Gson gson = (new GsonBuilder()).create();
            String rpcBeanId = dis.readUTF();
            rpcBeanId = rpcBeanId.substring(1, rpcBeanId.lastIndexOf("\""));

            RpcBeanDefinition rpcBeanDefinition = rpcServer.getRpcBeanFactory().getRpcBean(rpcBeanId);
            Method method = rpcBeanDefinition.getMethod();
            Object object = rpcBeanDefinition.getObject();

            String paraJson = dis.readUTF();
            Object[] para = gson.fromJson(paraJson, Object[].class);
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] parameters = new Object[parameterTypes.length];
            for (int i = 0; i < para.length; i++) {
                parameters[i] = gson.fromJson(para[i] + "", parameterTypes[i]);
            }
            Object result = method.invoke(object, parameters);
            dos.writeUTF(gson.toJson(result));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtil.close(dis, dos, socket);
        }
    }

}
