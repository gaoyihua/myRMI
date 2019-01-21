package com.gary.core;

import com.gary.util.CloseableUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.io.*;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
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
//    private ObjectInputStream ois;
//    private ObjectOutputStream oos;
    private DataInputStream dis;
    private DataOutputStream dos;

    public RpcServerExecutor(Socket socket, RpcServer rpcServer, ThreadPoolExecutor threadPool, long threadId) throws IOException {
        this.socket = socket;
        this.rpcServer = rpcServer;
//        this.ois = new ObjectInputStream(this.socket.getInputStream());
//        this.oos = new ObjectOutputStream(this.socket.getOutputStream());
        this.dis = new DataInputStream(this.socket.getInputStream());
        this.dos = new DataOutputStream(this.socket.getOutputStream());
        this.threadPool = threadPool;
        this.threadPool.execute(this);
        //new Thread(this, "RPC_EXECUTOR_" + threadId).start();
    }

    /**
     *  This method should be deleted.
     * @param parameters
     */
    private void showParameters(Object[] parameters) {
        for (int i = 0; i < parameters.length; i++) {
            System.out.println(i + ":" + parameters[i]);
        }
    }

    @Override
    public void run() {
        try {
            Gson gson = (new GsonBuilder()).create();
            //接收RPC客户端传递的id和参数
            //String rpcBeanId = ois.readUTF();
            //Object[] parameters = (Object[]) ois.readObject();
            String rpcBeanId = dis.readUTF();
            rpcBeanId = rpcBeanId.substring(1, rpcBeanId.lastIndexOf("\""));

            //showParameters(parameters);
            //定位相关类、对象、方法
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
            //反射执行
            Object result = method.invoke(object, parameters);
            //向RPC客户端返回执行结果
            //oos.writeObject(result);
            dos.writeUTF(gson.toJson(result));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtil.close(dis, dos, socket);
        }
    }

}
